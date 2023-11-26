package com.mise.seecooker.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.mise.seecooker.dao.CommentDao;
import com.mise.seecooker.dao.PostDao;
import com.mise.seecooker.dao.UserDao;
import com.mise.seecooker.entity.po.CommentPO;
import com.mise.seecooker.entity.po.PostPO;
import com.mise.seecooker.entity.po.UserPO;
import com.mise.seecooker.entity.vo.community.CommentVO;
import com.mise.seecooker.entity.vo.community.PostCommentVO;
import com.mise.seecooker.entity.vo.community.PostDetailVO;
import com.mise.seecooker.entity.vo.community.PostVO;
import com.mise.seecooker.enums.ImageType;
import com.mise.seecooker.exception.BizException;
import com.mise.seecooker.exception.ErrorType;
import com.mise.seecooker.service.PostService;
import com.mise.seecooker.util.AliOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 帖子业务服务层实现类
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Slf4j
@Service
public class PostServiceImpl implements PostService {
    private final PostDao postDao;
    private final UserDao userDao;
    private final CommentDao commentDao;

    @Autowired
    public PostServiceImpl(PostDao postDao, UserDao userDao, CommentDao commentDao) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.commentDao = commentDao;
    }

    @Override
    public Long addPost(String title, String content, MultipartFile[] images) throws IOException, ClientException {
        List<String> postImages = null;
        if (images != null) {
            postImages = new ArrayList<>();
            // 上传图片
            for (MultipartFile image : images) {
                String s = AliOSSUtil.uploadFile(image, ImageType.POST_IMAGE);
                postImages.add(s);
            }
        }
        Long posterId = StpUtil.getLoginIdAsLong();
        PostPO post = PostPO.builder()
                .title(title)
                .content(content)
                .posterId(posterId)
                .images(postImages)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        Long postId = postDao.save(post).getId();

        // 在poster发布的帖子内插入id
        UserPO poster = userDao.findById(posterId).get();
        poster.getPosts().add(postId);
        userDao.save(poster);

        return postId;
    }

    @Override
    public List<PostVO> getPosts() {
        List<PostPO> posts = postDao.findAll();
        posts.sort(Comparator.comparing(PostPO::getCreateTime));
        return posts.stream().map(postPO -> {
                    UserPO poster = userDao.findById(postPO.getPosterId()).get();
                    return PostVO.builder()
                            .postId(postPO.getId())
                            .title(postPO.getTitle())
                            .cover(postPO.getImages().isEmpty() ? null : postPO.getImages().get(0))
                            .posterName(poster.getUsername())
                            .posterAvatar(poster.getAvatar())
                            .build();
                }).toList();
    }

    @Override
    public PostDetailVO getPostDetail(Long id) {
        Optional<PostPO> post = postDao.findById(id);
        if (post.isEmpty()) {
            throw new BizException(ErrorType.POST_NOT_EXIST);
        }
        UserPO poster = userDao.findById(post.get().getPosterId()).get();
        return PostDetailVO.builder()
                .title(post.get().getTitle())
                .content(post.get().getContent())
                .images(post.get().getImages())
                .posterName(poster.getUsername())
                .posterAvatar(poster.getAvatar())
                .build();
    }

    @Override
    public Long addComment(PostCommentVO postComment) {
        CommentPO comment = CommentPO.builder()
                .postId(postComment.getPostId())
                .commenterId(StpUtil.getLoginIdAsLong())
                .content(postComment.getContent())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        comment = commentDao.save(comment);
        return comment.getId();
    }

    @Override
    public List<CommentVO> getCommentsByPostId(Long postId) {
        // 判断帖子是否存在
        if (!postDao.existsById(postId)) {
            throw new BizException(ErrorType.POST_NOT_EXIST);
        }
        List<CommentPO> commentPOs = commentDao.findAllByPostId(postId);
        return commentPOs.stream()
                .sorted(Comparator.comparing(CommentPO::getCreateTime))
                .map(commentPO -> {
                    UserPO commenter = userDao.findById(commentPO.getCommenterId()).get();
                    return CommentVO.builder()
                            .commenterName(commenter.getUsername())
                            .commenterAvatar(commenter.getAvatar())
                            .commentTime(commentPO.getCreateTime().format(DateTimeFormatter.ISO_DATE_TIME))
                            .content(commentPO.getContent())
                            .build();
                })
                .toList();
    }

}
