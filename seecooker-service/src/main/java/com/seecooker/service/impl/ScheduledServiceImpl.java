package com.seecooker.service.impl;

import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;
import com.seecooker.common.redis.enums.RedisKey;
import com.seecooker.dao.PostDao;
import com.seecooker.dao.UserDao;
import com.seecooker.pojo.po.PostPO;
import com.seecooker.pojo.po.UserPO;
import com.seecooker.service.ScheduledService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 定时任务实现类
 *
 * @author xueruichen
 * @date 2023.12.10
 */
@Slf4j
@Service
public class ScheduledServiceImpl implements ScheduledService {
    private final PostDao postDao;
    private final UserDao userDao;
    private final StringRedisTemplate redisTemplate;

    public ScheduledServiceImpl(PostDao postDao, UserDao userDao, StringRedisTemplate redisTemplate) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Scheduled(cron = "0 0/5 * * * *")
    public void scheduledUpdatePostLike() {
        log.info("Update post like information in database");
        String hashKey = RedisKey.POST_LIKE.getKey();
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashKey);
        entries.forEach((key, value) -> {
            // 从redis中解析帖子和用户id
            Long postId = Long.parseLong(((String)key).split("::")[0]);
            Long userId = Long.parseLong(((String)key).split("::")[1]);
            boolean res = Boolean.TRUE.toString().equals(value);
            // 从数据库中获取持久化对象
            Optional<PostPO> postOptional = postDao.findById(postId);
            if (postOptional.isEmpty()) {
                throw new BizException(ErrorType.POST_NOT_EXIST);
            }
            PostPO post = postOptional.get();
            Optional<UserPO> userOptional = userDao.findById(userId);
            if (userOptional.isEmpty()) {
                throw new BizException(ErrorType.USER_NOT_EXIST);
            }
            List<Long> likeList = post.getLikeUserIdList();
            boolean like = likeList.contains(userId);
            // 更新持久化对象
            if (!like && res) {
                // 用户先前未点赞，现在点赞了
                likeList.add(userId);
            }
            else if (like && !res) {
                // 用户先前点赞了，现在取消了点赞
                likeList.remove(userId);
            }
            post.setLikeUserIdList(likeList);
            postDao.save(post);
        });
    }
}
