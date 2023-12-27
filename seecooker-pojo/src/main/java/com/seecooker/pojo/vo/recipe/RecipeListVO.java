package com.seecooker.pojo.vo.recipe;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 菜谱预览VO类
 *
 * @author xueruichen
 * @date 2023.11.27
 */
@Getter
@Setter
@Builder
public class RecipeListVO {
    /**
     * 菜谱id
     */
    private Long id;

    /**
     * 菜谱名
     */
    private String name;

    /**
     * 菜谱封面url
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
     * 简介
     */
    private String introduction;

    /**
     * 评分
     */
    private Double score;

    /**
     * 是否收藏，未登陆默认False
     */
    private Boolean isFavorite;
}
