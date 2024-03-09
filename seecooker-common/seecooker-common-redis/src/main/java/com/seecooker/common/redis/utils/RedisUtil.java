package com.seecooker.common.redis.utils;

public class RedisUtil {
    public static final String INGREDIENT = "INGREDIENT";
    public static final String USER_LIKE_POST_STATE = "USER_LIKE_POST_STATE";
    public static final String USER_LIKE_POST_NUM = "USER_LIKE_POST_NUM";
    private RedisUtil(){}
    public static String getUserLikeKey(Long userId, Long postId) {
        StringBuffer sb = new StringBuffer();
        sb.append(userId).append("::").append(postId);
        return sb.toString();
    }

    public static String getPostLikeNumKey(Long postId) {
        return String.valueOf(postId);
    }
}
