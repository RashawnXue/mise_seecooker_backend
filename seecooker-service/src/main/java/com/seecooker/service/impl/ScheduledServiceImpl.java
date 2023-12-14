package com.seecooker.service.impl;

import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;
import com.seecooker.common.redis.enums.RedisKey;
import com.seecooker.dao.PostDao;
import com.seecooker.dao.RecipeDao;
import com.seecooker.dao.UserDao;
import com.seecooker.pojo.po.PostPO;
import com.seecooker.pojo.po.RecipePO;
import com.seecooker.pojo.po.UserPO;
import com.seecooker.service.ScheduledService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    private final RecipeDao recipeDao;
    private final RedisTemplate redisTemplate;

    public ScheduledServiceImpl(PostDao postDao, UserDao userDao, RecipeDao recipeDao, RedisTemplate redisTemplate) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.recipeDao = recipeDao;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Scheduled(cron = "0 0/5 * * * *")
    @Transactional
    public void scheduledUpdatePostLike() {
        log.info("Update post like information in database");
        String keyPrefix = RedisKey.POST_LIKE.getKey();
        Set<String> keys = redisTemplate.opsForHash().keys(keyPrefix + "*");
        keys.forEach(key -> {
            Map<String, Boolean> entries = redisTemplate.opsForHash().entries(key);
            entries.forEach((hashKey, value) -> {
                // 从redis中解析帖子和用户id
                Long postId = Long.parseLong(key.split(RedisKey.POST_LIKE_DELIMITER.getKey())[1]);
                Long userId = Long.parseLong(hashKey);
                boolean res = value;
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
                post.setUpdateTime(LocalDateTime.now());
                postDao.save(post);
            });
        });
        // 持久化完成后删除缓存
        redisTemplate.opsForHash().delete(keys);
        log.info("Finish update post like information in database");
    }

    @Override
    @Scheduled(cron = "0 0/10 * * * *")
    @Transactional
    public void scheduledUpdateRecipeFavorite() {
        log.info("Update recipe favorite information in database");
        String keyPrefix = RedisKey.RECIPE_FAVORITE.getKey();
        Set<String> keys = redisTemplate.opsForHash().keys(keyPrefix + "*");
        keys.forEach(key -> {
            Map<String, Boolean> entries = redisTemplate.opsForHash().entries(key);
            entries.forEach((hashKey, value) -> {
                // 从redis中解析帖子和用户id
                Long userId = Long.parseLong(key.split(RedisKey.POST_LIKE_DELIMITER.getKey())[1]);
                Long recipeId = Long.parseLong(hashKey);
                boolean res = value;
                // 从数据库中获取持久化对象
                Optional<RecipePO> recipeOptional = recipeDao.findById(recipeId);
                if (recipeOptional.isEmpty()) {
                    throw new BizException(ErrorType.RECIPE_NOT_EXIST);
                }
                RecipePO recipe = recipeOptional.get();
                Optional<UserPO> userOptional = userDao.findById(userId);
                if (userOptional.isEmpty()) {
                    throw new BizException(ErrorType.USER_NOT_EXIST);
                }
                UserPO user = userOptional.get();
                List<Long> favoriteList = user.getFavoriteRecipes();
                boolean favorite = favoriteList.contains(recipeId);
                // 更新持久化对象
                if (!favorite && res) {
                    // 用户先前未收藏，现在收藏了
                    favoriteList.add(recipeId);
                }
                else if (favorite && !res) {
                    // 用户先前点赞了，现在取消了点赞
                    favoriteList.remove(recipeId);
                }
                user.setFavoriteRecipes(favoriteList);
                user.setUpdateTime(LocalDateTime.now());
                userDao.save(user);
            });
        });
        // 持久化完成后删除缓存
        redisTemplate.opsForHash().delete(keys);
        log.info("Finish update recipe favorite information in database");
    }
}
