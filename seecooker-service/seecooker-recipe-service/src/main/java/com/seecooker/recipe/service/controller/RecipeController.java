package com.seecooker.recipe.service.controller;

import com.aliyuncs.exceptions.ClientException;
import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;
import com.seecooker.common.core.model.Result;
import com.seecooker.recipe.service.pojo.vo.*;
import com.seecooker.recipe.service.service.RecipeService;
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
@RequestMapping("/v2/")
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
    public Result<Void> publishRecipe(PublishRecipeVO publishRecipe, MultipartFile cover, MultipartFile[] stepImages) throws IOException, ClientException {
        if (stepImages == null) {
            throw new BizException(ErrorType.ILLEGAL_ARGUMENTS, "步骤图不能为空");
        }
        // 检查图片数量是否与步骤数量相等
        if (publishRecipe.getStepContents().size() != stepImages.length) {
            throw new BizException(ErrorType.RECIPE_STEP_MATCH_ERROR);
        }

        if (publishRecipe.getIngredients().size() != publishRecipe.getAmounts().size()) {
            throw new BizException(ErrorType.ILLEGAL_ARGUMENTS, "配料与量不匹配");
        }
        recipeService.addRecipe(publishRecipe, cover, stepImages);
        return Result.success();
    }

    /**
     * 获取菜谱列表
     *
     * @return 响应结果
     */
    @GetMapping("recipe/list")
    public Result<List<RecipeListVO>> getRecipes() {
        List<RecipeListVO> recipes = recipeService.getRecipes();
        return Result.success(recipes);
    }

    /**
     * 分页获取菜谱列表
     *
     * @param pageNo 页码
     * @return 响应结果
     */
    @GetMapping("recipe/list/page/{pageNo}")
    public Result<List<RecipeListVO>> getRecipesByPage(@PathVariable @NotNull Integer pageNo) {
        List<RecipeListVO> recipes = recipeService.getRecipesByPage(pageNo);
        return Result.success(recipes);
    }

    /**
     * 根据菜谱id获取菜谱细节
     *
     * @param recipeId 菜谱id
     * @return 菜谱细节VO类
     */
    @GetMapping("recipe/detail/{recipeId}")
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
    @GetMapping("recipe/search")
    public Result<List<RecipeListVO>> searchRecipes(@RequestParam @NotNull String query) {
        List<RecipeListVO> recipes = recipeService.getRecipesByNameLike(query);
        return Result.success(recipes);
    }

    /**
     * 收藏或取消收藏菜谱
     *
     * @param recipeId 菜谱id
     * @return 当前用户是否收藏菜谱
     */
    @PutMapping("recipe/favorite/{recipeId}")
    public Result<Boolean> favoriteRecipe(@PathVariable @NotNull Long recipeId) {
        Boolean result = recipeService.favoriteRecipe(recipeId);
        return Result.success(result);
    }

    /**
     * 菜谱评分
     *
     * @param recipeId 菜谱id
     * @param score 评分
     * @return 当前均分
     */
    @PostMapping("recipe/score")
    public Result<Double> scoreRecipe(Long recipeId, Double score) {
        double result = recipeService.scoreRecipe(recipeId, score);
        return Result.success(result);
    }

    /**
     * 根据用户id获取用户收藏菜谱
     *
     * @param userId 用户id
     * @return 用户收藏菜谱
     */
    @GetMapping("recipe/favorites/{userId}")
    public Result<List<RecipeListVO>> getFavoriteRecipes(@PathVariable @NotNull Long userId) {
        List<RecipeListVO> result = recipeService.getFavoriteRecipes(userId);
        return Result.success(result);
    }

    /**
     * 获取推荐菜谱
     *
     * @return 响应结果
     */
    @GetMapping("recipe/recommend")
    public Result<List<String>> recommendRecipe() {
        List<String> result = recipeService.getRandomRecipeName();
        return Result.success(result);
    }

    /**
     * 探索菜谱
     *
     * @param ingredients 配料
     * @return 响应结果
     */
    @GetMapping("recipe/explore")
    public Result<List<ExploreVO>> explore(@RequestBody @NotNull List<String> ingredients) {
        if (ingredients.isEmpty()) {
            throw new BizException(ErrorType.ILLEGAL_ARGUMENTS, "配料不能为空");
        }
        List<ExploreVO> result = recipeService.getRecipesByIngredient(ingredients);
        return Result.success(result);
    }

    /**
     * 获取配料列表
     *
     * @return 响应结果
     */
    @GetMapping("recipe/ingredients")
    public Result<List<IngredientVO>> getIngredients() {
        List<IngredientVO> ingredients = recipeService.getIngredients();
        return Result.success(ingredients);
    }

    /**
     * 获取发布的菜谱
     *
     * @param userId 用户id
     * @return 响应结果
     */
    @GetMapping("recipe/publish/{userId}")
    public Result<List<RecipeListVO>> getPublishRecipe(@PathVariable @NotNull Long userId) {
        List<RecipeListVO> result = recipeService.getPublishRecipe(userId);
        return Result.success(result);
    }
}
