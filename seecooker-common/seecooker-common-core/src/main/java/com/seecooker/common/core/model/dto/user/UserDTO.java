package com.seecooker.common.core.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用户数据传输类
 *
 * @author xueruichen
 * @date 2023.12.29
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDTO {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户头像url
     */
    private String avatar;

    /**
     * 用户收藏的菜谱id列表
     */
    private List<Long> favoriteRecipes;

    /**
     * 用户发布的菜谱id列表
     */
    private List<Long> postRecipes;

    /**
     * 用户发布的帖子id列表
     */
    private List<Long> posts;

    /**
     * 用户签名
     */
    private String signature;
}
