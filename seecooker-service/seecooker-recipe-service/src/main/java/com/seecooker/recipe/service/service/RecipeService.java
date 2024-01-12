package com.seecooker.recipe.service.service;

import com.aliyuncs.exceptions.ClientException;
import com.seecooker.recipe.service.pojo.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 菜谱业务服务层接口类
 *
 * @author xueruichen
 * @date 2023.11.27
 */
public interface RecipeService {
    /**
     * 添加菜谱
     *
     * @param publishRecipe VO类
     * @param cover 封面图
     * @param stepImages 步骤图
     */
    void addRecipe(PublishRecipeVO publishRecipe, MultipartFile cover, MultipartFile[] stepImages) throws IOException, ClientException;

    /**
     * 获取菜谱列表
     *
     * @return 菜谱VO列表
     */
    List<RecipeListVO> getRecipes();

    /**
     * 根据菜谱id获取菜谱细节VO类
     *
     * @param recipeId 菜谱id
     * @return 菜谱细节VO类
     */
    RecipeDetailVO getRecipeDetailById(Long recipeId);

    /**
     * 根据关键词查找菜谱
     *
     * @param query 关键词
     * @return 菜谱列表
     */
    List<RecipeListVO> getRecipesByNameLike(String query);

    /**
     * 收藏或取消收藏菜谱
     *
     * @param recipeId 菜谱id
     * @return 菜谱状态
     */
    Boolean favoriteRecipe(Long recipeId);

    /**
     * 菜谱评分
     *
     * @param recipeId 菜谱id
     * @param score 评分
     * @return 菜谱均分
     */
    double scoreRecipe(Long recipeId, Double score);

    /**
     * 根据页码获取菜谱
     *
     * @param pageNo 页码数
     * @return 结果
     */
    List<RecipeListVO> getRecipesByPage(Integer pageNo);

    /**
     * 获取用户收藏的菜谱
     *
     * @param userId 用户id
     * @return 结果
     */
    List<RecipeListVO> getFavoriteRecipes(Long userId);

    /**
     * 获取随机菜谱名
     *
     * @return 结果
     */
    List<String> getRandomRecipeName();

    /**
     * 根据配料获取菜谱
     *
     * @param ingredients 配料
     * @return 结果
     */
    List<ExploreVO> getRecipesByIngredient(List<String> ingredients);

    /**
     * 获取配料
     *
     * @return 结果
     */
    List<IngredientVO> getIngredients();

    /**
     * 获取用户发布的菜谱
     *
     * @param userId 用户id
     * @return 结果
     */
    List<RecipeListVO> getPublishRecipe(Long userId);
}
