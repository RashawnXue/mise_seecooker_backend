package com.seecooker.recipe.service.pojo.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 菜谱细节VO类
 *
 * @author xueruichen
 * @date 2023.11.27
 */
@Getter
@Setter
@Builder
public class RecipeDetailVO {
    /**
     * 菜谱名
     */
    private String name;

    /**
     * 菜谱封面url
     */
    private String cover;

    /**
     * 菜谱介绍
     */
    private String introduction;

    /**
     * 步骤图url
     */
    private List<String> stepImages;

    /**
     * 操作步骤
     */
    private List<String> stepContents;

    /**
     * 作者id
     */
    private Long authorId;

    /**
     * 作者名
     */
    private String authorName;

    /**
     * 作者头像url
     */
    private String authorAvatar;

    /**
     * 是否收藏，为登陆默认为false
     */
    private Boolean favorite;

    /**
     * 是否已评分
     */
    private Boolean scored;

    /**
     * 评分，未登陆和未评分默认为0
     */
    private Double score;

    /**
     * 平均分
     */
    private Double averageScore;

    /**
     * 配料量
     */
    private Map<String, String> ingredientAmounts;

    /**
     * 发布时间
     */
    private String publishTime;
}
