package com.seecooker.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.seecooker.common.core.enums.ImageType;
import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;
import com.seecooker.common.redis.enums.RedisKey;
import com.seecooker.dao.CommentDao;
import com.seecooker.dao.PostDao;
import com.seecooker.dao.UserDao;
import com.seecooker.oss.util.AliOSSUtil;
import com.seecooker.pojo.po.CommentPO;
import com.seecooker.pojo.po.PostPO;
import com.seecooker.pojo.po.UserPO;
import com.seecooker.pojo.vo.community.CommentVO;
import com.seecooker.pojo.vo.community.PostCommentVO;
import com.seecooker.pojo.vo.community.PostDetailVO;
import com.seecooker.pojo.vo.community.PostVO;
import com.seecooker.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
    private final RedisTemplate redisTemplate;

    public PostServiceImpl(PostDao postDao, UserDao userDao, CommentDao commentDao, RedisTemplate redisTemplate) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.commentDao = commentDao;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Long addPost(String title, String content, MultipartFile[] images) throws IOException, ClientException {
        List<String> postImages = AliOSSUtil.uploadFile(images, ImageType.POST_IMAGE);
        Long posterId = StpUtil.getLoginIdAsLong();
        PostPO post = PostPO.builder()
                .title(title)
                .content(content)
                .posterId(posterId)
                .images(postImages)
                .likeUserIdList(Collections.emptyList())
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

        // 检查是否登陆
        boolean isLogin = StpUtil.isLogin();
        boolean like = false;

        List<Long> likeUsersId = post.get().getLikeUserIdList();
        Long likeNum = (long) likeUsersId.size();

        String key = RedisKey.POST_LIKE.getKey() + RedisKey.POST_LIKE_DELIMITER.getKey() + post.get().getId();
        likeNum += redisTemplate.opsForHash().entries(key).values().stream().filter(v->(Boolean)v).count();

        // 是否已点赞
        if (isLogin) {
            Long userId = StpUtil.getLoginIdAsLong();
            String hashKey = userId.toString();
            Boolean hashResult = (Boolean) redisTemplate.opsForHash().get(key, hashKey);

            if (Boolean.FALSE.equals(hashResult)) {
                // 若缓存中为false，则为false
                like = false;
            }
            else if (Boolean.TRUE.equals(hashResult) || likeUsersId.contains(userId)) {
                // 若缓存中或数据库中有点赞，则为true
                like = true;
                redisTemplate.opsForHash().put(hashKey, key, Boolean.TRUE);
            }
        }

        return PostDetailVO.builder()
                .title(post.get().getTitle())
                .content(post.get().getContent())
                .images(post.get().getImages())
                .posterName(poster.getUsername())
                .posterAvatar(poster.getAvatar())
                .like(like) // 未登陆默认为false
                .likeNum(likeNum)
                .publishTime(post.get().getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();
    }

    @Override
    public CommentVO addComment(PostCommentVO postComment) {
        CommentPO comment = CommentPO.builder()
                .postId(postComment.getPostId())
                .commenterId(StpUtil.getLoginIdAsLong())
                .content(postComment.getContent())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        comment = commentDao.save(comment);
        return commentMapper(comment);
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
                .map(this::commentMapper)
                .toList();
    }

    @Override
    public Boolean likePost(Long postId) {
        long userId = StpUtil.getLoginIdAsLong();
        // 在redis中的hash进行记录，结构：hashKey--POST_LIKE::postId, key--userId, value--boolean
        String key = RedisKey.POST_LIKE.getKey() + RedisKey.POST_LIKE_DELIMITER.getKey() + postId;
        String hashKey = Long.toString(userId);
        // 获取对应记录, value表示当前用户是否已经点赞
        Boolean value = (Boolean) redisTemplate.opsForHash().get(key, hashKey);

        // 无记录，读取数据库
        if (value == null) {
            Optional<PostPO> postOptional = postDao.findById(postId);
            if (postOptional.isEmpty()) {
                throw new BizException(ErrorType.POST_NOT_EXIST);
            }
            value = postOptional.get().getLikeUserIdList().contains(userId);
        }

        if (Boolean.TRUE.equals(value)) {
            // 已点赞，删除记录
            redisTemplate.opsForHash().put(key, hashKey, Boolean.FALSE);
        } else {
            // 未点赞，添加记录
            redisTemplate.opsForHash().put(key, hashKey, Boolean.TRUE);
        }

        return Boolean.FALSE.equals(value);
    }

    private CommentVO commentMapper(CommentPO commentPO) {
        UserPO commenter = userDao.findById(commentPO.getCommenterId()).get();
        return CommentVO.builder()
                .commenterName(commenter.getUsername())
                .commenterAvatar(commenter.getAvatar())
                .commentTime(commentPO.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .content(commentPO.getContent())
                .build();
    }
}
