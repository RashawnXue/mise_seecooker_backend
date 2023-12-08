package com.seecooker.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.seecooker.pojo.po.PostPO;
import com.seecooker.pojo.po.UserPO;
import com.seecooker.pojo.vo.community.CommentVO;
import com.seecooker.pojo.vo.community.PostCommentVO;
import com.seecooker.pojo.vo.community.PostDetailVO;
import com.seecooker.pojo.vo.community.PostVO;
import com.seecooker.common.enums.ImageType;
import com.seecooker.common.exception.BizException;
import com.seecooker.common.exception.ErrorType;
import com.seecooker.dao.CommentDao;
import com.seecooker.dao.PostDao;
import com.seecooker.dao.UserDao;
import com.seecooker.oss.util.AliOSSUtil;
import com.seecooker.pojo.po.CommentPO;
import com.seecooker.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public PostServiceImpl(PostDao postDao, UserDao userDao, CommentDao commentDao) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.commentDao = commentDao;
    }

    @Override
    public Long addPost(String title, String content, MultipartFile[] images) throws IOException, ClientException {
        List<String> postImages = null;
        if (images != null) {
            postImages = AliOSSUtil.uploadFile(images, ImageType.POST_IMAGE);
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
                            .commentTime(commentPO.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                            .content(commentPO.getContent())
                            .build();
                })
                .toList();
    }

}
