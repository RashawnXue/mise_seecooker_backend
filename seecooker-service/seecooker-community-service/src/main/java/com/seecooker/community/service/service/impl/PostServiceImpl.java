package com.seecooker.community.service.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.seecooker.common.core.enums.ImageType;
import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;

import com.seecooker.common.core.model.Result;
import com.seecooker.common.core.model.dto.user.UserDTO;
import com.seecooker.community.service.dao.CommentDao;
import com.seecooker.community.service.dao.PostDao;
import com.seecooker.community.service.pojo.po.CommentPO;
import com.seecooker.community.service.pojo.po.PostPO;
import com.seecooker.community.service.pojo.vo.CommentVO;
import com.seecooker.community.service.pojo.vo.PostCommentVO;
import com.seecooker.community.service.pojo.vo.PostDetailVO;
import com.seecooker.community.service.pojo.vo.PostListVO;
import com.seecooker.community.service.service.PostService;
import com.seecooker.feign.user.UserClient;
import com.seecooker.util.oss.AliOSSUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 帖子业务服务层实现类
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Slf4j
@Service
@Transactional
public class PostServiceImpl implements PostService {
    private final PostDao postDao;
    private final CommentDao commentDao;
    private final UserClient userClient;
    private final int pageSize = 8;

    public PostServiceImpl(PostDao postDao, CommentDao commentDao, UserClient userClient) {
        this.postDao = postDao;
        this.commentDao = commentDao;
        this.userClient = userClient;
    }

    @Override
    public void addPost(String title, String content, MultipartFile[] images) throws IOException, ClientException {
        List<String> postImages = AliOSSUtil.uploadFile(images, ImageType.POST_IMAGE);
        Long posterId = StpUtil.getLoginIdAsLong();
        PostPO post = PostPO.builder()
                .title(title)
                .content(content)
                .posterId(posterId)
                .images(postImages)
                .likeUserIdList(Collections.emptyList())
                .commentIdList(Collections.emptyList())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        Long postId = postDao.save(post).getId();

        // 在poster发布的帖子内插入id
        UserDTO poster = getUser(posterId);
        poster.getPosts().add(postId);

        Result<Void> saveResult = userClient.updateUserPosts(posterId, poster.getPosts());
        if (saveResult.fail()) {
            throw new BizException(ErrorType.OPEN_FEIGN_API_ERROR);
        }
    }

    @Override
    public List<PostListVO> getPosts() {
        List<PostPO> posts = postDao.findAll();
        posts.sort(Comparator.comparing(PostPO::getCreateTime));

        return mapPost(posts);
    }

    @Override
    public PostDetailVO getPostDetail(Long id) {
        Optional<PostPO> postOp = postDao.findById(id);
        if (postOp.isEmpty()) {
            throw new BizException(ErrorType.POST_NOT_EXIST);
        }
        PostPO post = postOp.get();

        UserDTO poster = getUser(post.getPosterId());

        // 检查是否登陆
        boolean isLogin = StpUtil.isLogin();
        boolean like = false;

        // 是否已点赞
        if (isLogin) {
            like = post.getLikeUserIdList().contains(StpUtil.getLoginIdAsLong());
        }

        List<Long> likeUsersId = post.getLikeUserIdList();
        int likeNum = likeUsersId.size();

        return PostDetailVO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .images(post.getImages())
                .posterId(poster.getId())
                .posterName(poster.getUsername())
                .posterAvatar(poster.getAvatar())
                .like(like) // 未登陆默认为false
                .likeNum(likeNum)
                .publishTime(post.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();
    }

    @Override
    public CommentVO addComment(PostCommentVO postComment) {
        Optional<PostPO> postOp = postDao.findById(postComment.getPostId());
        if (postOp.isEmpty()) {
            throw new BizException(ErrorType.POST_NOT_EXIST);
        }
        PostPO post = postOp.get();
        CommentPO comment = CommentPO.builder()
                .commenterId(StpUtil.getLoginIdAsLong())
                .content(postComment.getContent())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        comment = commentDao.save(comment);
        // 将评论id存入post
        post.getCommentIdList().add(comment.getId());
        postDao.save(post);
        return commentMapper(comment);
    }

    @Override
    public List<CommentVO> getCommentsByPostId(Long postId) {
        // 判断帖子是否存在
        if (!postDao.existsById(postId)) {
            throw new BizException(ErrorType.POST_NOT_EXIST);
        }
        PostPO post = postDao.findById(postId).get();

        List<CommentPO> commentPOs = commentDao.findAllById(post.getCommentIdList());
        return commentPOs.stream()
                .sorted(Comparator.comparing(CommentPO::getCreateTime))
                .map(this::commentMapper)
                .toList();
    }

    @Override
    public Boolean likePost(Long postId) {
        long userId = StpUtil.getLoginIdAsLong();
        boolean like = false;
        Optional<PostPO> postOp = postDao.findById(postId);
        if (postOp.isEmpty()) {
            throw new BizException(ErrorType.POST_NOT_EXIST);
        }
        PostPO post = postOp.get();

        if (!post.getLikeUserIdList().contains(userId)) {
            like = true;
            post.getLikeUserIdList().add(userId);
            postDao.save(post);
        }

        return like;
    }

    @Override
    public void deletePost(Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        Optional<PostPO> postOp = postDao.findById(id);
        if (postOp.isEmpty()) {
            throw new BizException(ErrorType.POST_NOT_EXIST);
        }
        PostPO post = postOp.get();
        if (!Objects.equals(post.getPosterId(), userId)) {
            throw new BizException(ErrorType.UNAUTHORIZED, "用户不能删除其他人发布的帖子");
        }
        postDao.delete(post);

        UserDTO user = getUser(userId);
        user.getPosts().remove(id);
        Result<Void> updateResult = userClient.updateUserPosts(userId, user.getPosts());
        if (updateResult.fail()) {
            throw new BizException(ErrorType.OPEN_FEIGN_API_ERROR);
        }
    }

    @Override
    public List<PostListVO> getUserPosts(Long userId) {
        UserDTO user = getUser(userId);
        List<PostPO> posts = postDao.findAllById(user.getPosts());
        return mapPost(posts);
    }

    @Override
    public List<PostListVO> getPostsByPage(Integer pageNo) {
        List<PostPO> posts = postDao.findAll(PageRequest.of(pageNo, pageSize)).stream().toList();
        return mapPost(posts);
    }

    private CommentVO commentMapper(CommentPO commentPO) {
        // 评论VO的映射
        UserDTO commenter = getUser(commentPO.getCommenterId());
        return CommentVO.builder()
                .commenterId(commenter.getId())
                .commenterName(commenter.getUsername())
                .commenterAvatar(commenter.getAvatar())
                .commentTime(commentPO.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .content(commentPO.getContent())
                .build();
    }

    private UserDTO getUser(Long userId) {
        // 调用feign-api 封装方法
        Result<UserDTO> userResult = userClient.getUserById(userId);
        if (userResult.fail()) {
            throw new BizException(ErrorType.OPEN_FEIGN_API_ERROR);
        }
        return userResult.getData();
    }

    private List<PostListVO> mapPost(List<PostPO> posts) {
        // 将postPOList映射
        boolean isLogin = StpUtil.isLogin();
        UserDTO currentUser;
        if (isLogin) {
            currentUser = getUser(StpUtil.getLoginIdAsLong());
        } else {
            currentUser = null;
        }
        return posts.stream().map(postPO -> {
            UserDTO poster = getUser(postPO.getPosterId());

            boolean like = false;
            if (isLogin) {
                like = postPO.getLikeUserIdList().contains(currentUser.getId());
            }

            return PostListVO.builder()
                    .postId(postPO.getId())
                    .title(postPO.getTitle())
                    .cover(postPO.getImages().isEmpty() ? null : postPO.getImages().get(0))
                    .posterId(poster.getId())
                    .posterName(poster.getUsername())
                    .posterAvatar(poster.getAvatar())
                    .like(like)
                    .likeNum(postPO.getLikeUserIdList().size())
                    .publishTime(postPO.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .commentNum(postPO.getCommentIdList().size())
                    .content(postPO.getContent())
                    .build();
        }).toList();
    }
}
