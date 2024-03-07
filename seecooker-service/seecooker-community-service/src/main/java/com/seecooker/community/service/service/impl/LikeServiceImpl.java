package com.seecooker.community.service.service.impl;

import com.seecooker.common.redis.utils.RedisUtil;
import com.seecooker.community.service.dao.PostDao;
import com.seecooker.community.service.dao.UserLikeDao;
import com.seecooker.community.service.pojo.dto.LikeNumDTO;
import com.seecooker.community.service.pojo.dto.UserLikeDTO;
import com.seecooker.community.service.pojo.po.PostPO;
import com.seecooker.community.service.pojo.po.UserLikePO;
import com.seecooker.community.service.service.LikeService;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LikeServiceImpl implements LikeService {
    private final PostDao postDao;
    private final UserLikeDao userLikeDao;
    private final RedisTemplate redisTemplate;

    public LikeServiceImpl(PostDao postDao, UserLikeDao userLikeDao, RedisTemplate redisTemplate) {
        this.postDao = postDao;
        this.userLikeDao = userLikeDao;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public void transLikeFromRedis2DB() {
        List<UserLikeDTO> list = getLikeDataFromRedis();
        for (UserLikeDTO userLikeDTO : list) {
            UserLikePO userLikePO = userLikeDao.getUserLikePOByUserIdAndPostId(userLikeDTO.getUserId(), userLikeDTO.getPostId());
            if (userLikePO == null) {
                userLikeDao.save(UserLikePO.builder()
                                .userId(userLikeDTO.getUserId())
                                .postId(userLikeDTO.getPostId())
                                .status(userLikeDTO.getStatus())
                                .createTime(LocalDateTime.now())
                                .updateTime(LocalDateTime.now())
                                .build());
            } else {
                userLikePO.setStatus(userLikeDTO.getStatus());
                userLikeDao.save(userLikePO);
            }
        }
    }

    @Override
    @Transactional
    public void transLikeNumFromRedis2DB() {
        List<LikeNumDTO> list = getLikeNumFromRedis();
        for (LikeNumDTO likeNumDTO : list) {
            PostPO post = postDao.getReferenceById(likeNumDTO.getPostId());
            if (post != null) {
                Integer likeNum = likeNumDTO.getLikeNum();
                post.setLikeNum(likeNum);
                postDao.save(post);
            }
        }
    }

    private List<UserLikeDTO> getLikeDataFromRedis() {
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(RedisUtil.USER_LIKE_POST_STATE, ScanOptions.NONE);
        List<UserLikeDTO> result = new ArrayList<>();
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> entry = cursor.next();
            String key = (String) entry.getKey();
            String[] split = key.split("::");
            Long userId = Long.parseLong(split[0]);
            Long postId = Long.parseLong(split[1]);
            Boolean status = (Boolean)entry.getValue();

            UserLikeDTO userLikeDTO = UserLikeDTO.builder().userId(userId).postId(postId).status(status).build();
            result.add(userLikeDTO);
            redisTemplate.opsForHash().delete(RedisUtil.USER_LIKE_POST_STATE, key);
        }
        return result;
    }

    private List<LikeNumDTO> getLikeNumFromRedis() {
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(RedisUtil.USER_LIKE_POST_NUM, ScanOptions.NONE);
        List<LikeNumDTO> result = new ArrayList<>();
        while(cursor.hasNext()) {
            Map.Entry<Object, Object> entry = cursor.next();
            String key = (String) entry.getKey();
            Long postId = Long.parseLong(key);
            Integer likeNum = (Integer) entry.getValue();

            LikeNumDTO likeNumDTO = LikeNumDTO.builder().postId(postId).likeNum(likeNum).build();
            result.add(likeNumDTO);
            redisTemplate.opsForHash().delete(RedisUtil.USER_LIKE_POST_NUM, key);
        }
        return result;
    }
}
