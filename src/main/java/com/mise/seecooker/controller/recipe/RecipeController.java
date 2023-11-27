package com.mise.seecooker.controller.recipe;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.mise.seecooker.entity.Result;
import com.mise.seecooker.entity.vo.recipe.PublishRecipeVO;
import com.mise.seecooker.entity.vo.recipe.RecipeDetailVO;
import com.mise.seecooker.entity.vo.recipe.RecipeVO;
import com.mise.seecooker.exception.BizException;
import com.mise.seecooker.exception.ErrorType;
import com.mise.seecooker.service.RecipeService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 菜谱控制层类
 *
 * @author xueruichen
 * @date 2023.11.27
 */
@Slf4j
@RestController
@RequestMapping("/v1/")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * 发布菜谱
     *
     * @param publishRecipe 发布菜谱VO类
     * @param cover 菜谱封面图
     * @param stepImages 菜谱步骤图
     * @return 响应结果
     */
    @PostMapping("recipe")
    public Result<Long> publicRecipe(PublishRecipeVO publishRecipe, MultipartFile cover, MultipartFile[] stepImages) throws IOException, ClientException {
        // 未登陆不能发布菜谱
        StpUtil.checkLogin();
        // 检查图片数量是否与步骤数量相等
        if (publishRecipe.getStepContents().size() != stepImages.length) {
            throw new BizException(ErrorType.RECIPE_STEP_MATCH_ERROR);
        }
        Long id = recipeService.addRecipe(publishRecipe, cover, stepImages);
        return Result.success(id);
    }

    /**
     * 获取菜谱列表
     *
     * @return 响应结果
     */
    @GetMapping("recipes")
    public Result<List<RecipeVO>> getRecipes() {
        List<RecipeVO> recipes = recipeService.getRecipes();
        return Result.success(recipes);
    }

    /**
     * 根据菜谱id获取菜谱细节
     *
     * @param recipeId 菜谱id
     * @return 菜谱细节VO类
     */
    @GetMapping("recipe/{recipeId}")
    public Result<RecipeDetailVO> getRecipeDetail(@PathVariable @NotNull Long recipeId) {
        RecipeDetailVO recipeDetail = recipeService.getRecipeDetailById(recipeId);
        return Result.success(recipeDetail);
    }

    /**
     * 搜索菜谱
     *
     * @param query 搜索关键词
     * @return 响应结果
     */
    @GetMapping("recipes/search")
    public Result<List<RecipeVO>> searchRecipes(@RequestParam @NotNull String query) {
        List<RecipeVO> recipes = recipeService.getRecipesByNameLike(query);
        return Result.success(recipes);
    }
}
