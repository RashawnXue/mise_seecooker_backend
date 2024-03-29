package com.seecooker.user.service.pojo.vo;

import lombok.Builder;
import lombok.Getter;

/**
 * 用户信息VO类
 *
 * @author xueruichen
 * @date 2023.11.23
 */
@Getter
@Builder
public class UserInfoVO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像url
     */
    private String avatar;

    /**
     * 发布帖子数
     */
    private Integer postNum;

    /**
     * 获赞数
     */
    private Integer getLikedNum;

    /**
     * 用户签名
     */
    private String signature;
}
