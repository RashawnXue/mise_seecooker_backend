package com.seecooker.community.service.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.seecooker.common.core.enums.ImageType;
import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;
import com.seecooker.common.core.model.Result;
import com.seecooker.common.core.model.dto.user.UserDTO;
import com.seecooker.common.redis.utils.RedisUtil;
import com.seecooker.community.service.dao.CommentDao;
import com.seecooker.community.service.dao.PostDao;
import com.seecooker.community.service.dao.UserLikeDao;
import com.seecooker.community.service.esdao.EsPostDao;
import com.seecooker.community.service.pojo.po.CommentPO;
import com.seecooker.community.service.pojo.po.EsPostPO;
import com.seecooker.community.service.pojo.po.PostPO;
import com.seecooker.community.service.pojo.po.UserLikePO;
import com.seecooker.community.service.pojo.vo.*;
import com.seecooker.community.service.service.PostService;
import com.seecooker.feign.user.UserClient;
import com.seecooker.util.oss.AliOSSUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate redisTemplate;
    private final UserLikeDao userLikeDao;
    private final EsPostDao esPostDao;

    public PostServiceImpl(PostDao postDao, CommentDao commentDao, UserClient userClient, RedisTemplate redisTemplate, UserLikeDao userLikeDao, EsPostDao esPostDao) {
        this.postDao = postDao;
        this.commentDao = commentDao;
        this.userClient = userClient;
        this.redisTemplate = redisTemplate;
        this.userLikeDao = userLikeDao;
        this.esPostDao = esPostDao;
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
                .likeNum(0)
                .commentIdList(Collections.emptyList())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        PostPO savedPost = postDao.save(post);
        Long postId = savedPost.getId();
        esPostDao.save(EsPostPO.builder()
                        .id(savedPost.getId())
                        .content(savedPost.getContent())
                        .title(savedPost.getTitle())
                        .createTime(savedPost.getCreateTime())
                        .build());

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

        String postLikeNumKey = RedisUtil.getPostLikeNumKey(id);

        // 检查是否登陆
        boolean isLogin = StpUtil.isLogin();
        boolean like = false;

        // 是否已点赞
        if (isLogin) {
            String key = RedisUtil.getUserLikeKey(StpUtil.getLoginIdAsLong(), id);
            if (redisTemplate.opsForHash().hasKey(RedisUtil.USER_LIKE_POST_STATE, key)) {
                like = (boolean)redisTemplate.opsForHash().get(RedisUtil.USER_LIKE_POST_STATE, key);
            } else {
                UserLikePO userLike = userLikeDao.getUserLikePOByUserIdAndPostId(StpUtil.getLoginIdAsLong(), id);
                if (userLike != null) {
                    like = userLike.getStatus();
                }
                redisTemplate.opsForHash().put(RedisUtil.USER_LIKE_POST_STATE, key, like);
            }
        }

        int likeNum;
        if (redisTemplate.opsForHash().hasKey(RedisUtil.USER_LIKE_POST_NUM, postLikeNumKey)) {
            likeNum = (int)redisTemplate.opsForHash().get(RedisUtil.USER_LIKE_POST_NUM, postLikeNumKey);
        } else {
            likeNum = post.getLikeNum();
            redisTemplate.opsForHash().put(RedisUtil.USER_LIKE_POST_NUM, postLikeNumKey, likeNum);
        }

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
    public Boolean likePost(Long userId, Long postId) {
        boolean like = false;
        int likeNum = 0;
        Optional<PostPO> postOp = postDao.findById(postId);
        if (postOp.isEmpty()) {
            throw new BizException(ErrorType.POST_NOT_EXIST);
        }
        PostPO post = postOp.get();

        String userLikePostKey = RedisUtil.getUserLikeKey(userId, postId);
        String postLikeNumKey = RedisUtil.getPostLikeNumKey(postId);

//        // 判断缓存是否有点赞状态和点赞数
        if (redisTemplate.opsForHash().hasKey(RedisUtil.USER_LIKE_POST_STATE, userLikePostKey)) {
            like = (boolean)redisTemplate.opsForHash().get(RedisUtil.USER_LIKE_POST_STATE, userLikePostKey);
        } else {
            UserLikePO userLikePO = userLikeDao.getUserLikePOByUserIdAndPostId(userId, postId);
            if (userLikePO != null) {
                like = userLikePO.getStatus();
            }
        }
//
        if (redisTemplate.opsForHash().hasKey(RedisUtil.USER_LIKE_POST_NUM, postLikeNumKey)) {
            likeNum = (int)redisTemplate.opsForHash().get(RedisUtil.USER_LIKE_POST_NUM, postLikeNumKey);
        } else {
            likeNum = post.getLikeNum();
        }
        // 进行相应修改
        redisTemplate.opsForHash().put(RedisUtil.USER_LIKE_POST_STATE, userLikePostKey, !like);
        redisTemplate.opsForHash().put(RedisUtil.USER_LIKE_POST_NUM, postLikeNumKey, likeNum + (like ? -1 : 1));

//        if (!post.getLikeUserIdList().contains(userId)) {
//            like = true;
//            post.getLikeUserIdList().add(userId);
//            postDao.save(post);
//        }

        return !like;
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

    @Override
    public List<EsPostVO> searchPosts(String keyword) {
        SearchHits<EsPostPO> hits = esPostDao.find(keyword);
        List<EsPostVO> result = new ArrayList<>();
        for (SearchHit<EsPostPO> hit : hits) {
            EsPostPO po = hit.getContent();
            result.add(EsPostVO.builder()
                            .id(po.getId())
                            .content(po.getContent())
                            .title(po.getTitle())
                            .createTime(po.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .build());
            log.info("highlight field: " + hit.getHighlightFields());
        }
        return result;
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
                UserLikePO userLikePO = userLikeDao.getUserLikePOByUserIdAndPostId(StpUtil.getLoginIdAsLong(), postPO.getId());
                if (userLikePO != null) {
                    like = userLikePO.getStatus();
                }
            }

            return PostListVO.builder()
                    .postId(postPO.getId())
                    .title(postPO.getTitle())
                    .cover(postPO.getImages().isEmpty() ? null : postPO.getImages().get(0))
                    .posterId(poster.getId())
                    .posterName(poster.getUsername())
                    .posterAvatar(poster.getAvatar())
                    .like(like)
                    .likeNum(postPO.getLikeNum())
                    .publishTime(postPO.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .commentNum(postPO.getCommentIdList().size())
                    .content(postPO.getContent())
                    .build();
        }).toList();
    }
}
