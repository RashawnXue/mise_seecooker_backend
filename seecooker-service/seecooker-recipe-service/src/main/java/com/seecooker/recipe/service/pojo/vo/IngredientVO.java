package com.seecooker.recipe.service.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 配料VO类
 *
 * @author xueruichen
 * @date 2024.01.11
 */
@Data
@Builder
public class IngredientVO {
    /**
     * 种类
     */
    private String category;

    /**
     * 名称
     */
    private List<String> name;
}
