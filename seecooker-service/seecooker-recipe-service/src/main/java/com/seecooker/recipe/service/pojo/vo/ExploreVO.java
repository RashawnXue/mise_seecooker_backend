package com.seecooker.recipe.service.pojo.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 发现菜谱VO来
 *
 * @author xueruichen
 * @date 2024.01.11
 */
@Getter
@Setter
@Builder
public class ExploreVO {
    /**
     * id
     */
    private Long recipeId;

    /**
     * 菜谱名
     */
    private String name;

    /**
     * 介绍
     */
    private String introduction;

    /**
     * 封面图
     */
    private String cover;

    /**
     * 作者名
     */
    private String authorName;

    /**
     * 作者头像
     */
    private String authorAvatar;

    /**
     * 是否收藏
     */
    private Boolean favorite;
}
