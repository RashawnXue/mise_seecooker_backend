package com.seecooker.common.redis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Redis key 枚举类
 *
 * @author xueruichen
 * @date 2023.12.10
 */
@AllArgsConstructor
@Getter
public enum RedisKey {
    POST_LIKE("POST_LIKE"), // 帖子点赞
    POST_LIKE_DELIMITER("::"); // 帖子点赞分隔符

    private final String key;
}
