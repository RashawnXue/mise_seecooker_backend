package com.seecooker.pojo.vo.recipe;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


/**
 * 发布菜谱VO类
 *
 * @author xueruichen
 * @date 2023.11.27
 */
@Getter
@Setter
@Builder
public class PublishRecipeVO {
    /**
     * 菜谱名
     */
    @NotNull
    private String name;

    /**
     * 菜谱介绍
     */
    @NotNull
    @Size(min = 5, max = 500)
    private String introduction;

    /**
     * 操作步骤
     */
    @NotNull
    private List<String> stepContents;

    /**
     * 所需配料
     */
    @NotNull
    private List<String> ingredients;

    /**
     * 配料量
     */
    @NotNull
    private List<String> amounts;
}
