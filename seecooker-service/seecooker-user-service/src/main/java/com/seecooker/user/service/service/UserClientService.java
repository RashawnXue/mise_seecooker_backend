package com.seecooker.user.service.service;

import com.seecooker.common.core.model.dto.user.UserDTO;

import java.util.List;

/**
 * feign-api 用户服务
 *
 * @author xueruichen
 * @date 2023.12.29
 */
public interface UserClientService {
    /**
     * 获取UserDTO
     *
     * @param id userId
     * @return 结果
     */
    UserDTO getUserById(Long id);

    /**
     * 更新发布的菜谱
     *
     * @param userId 用户id
     * @param recipes 发布菜谱
     */
    void updatePostRecipes(Long userId, List<Long> recipes);

    /**
     * 修改用户对菜谱的收藏状态
     *
     * @param userId 用户id
     * @param recipeId 菜谱id
     * @return 用户是否收藏菜谱
     */
    Boolean updateFavoriteRecipe(Long userId, Long recipeId);
}
