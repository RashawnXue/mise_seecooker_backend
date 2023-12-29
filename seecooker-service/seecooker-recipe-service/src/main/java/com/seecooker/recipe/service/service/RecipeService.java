package com.seecooker.recipe.service.service;

import com.aliyuncs.exceptions.ClientException;
import com.seecooker.recipe.service.pojo.vo.PublishRecipeVO;
import com.seecooker.recipe.service.pojo.vo.RecipeDetailVO;
import com.seecooker.recipe.service.pojo.vo.RecipeListVO;
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
     * @return 添加菜谱id
     */
    Long addRecipe(PublishRecipeVO publishRecipe, MultipartFile cover, MultipartFile[] stepImages) throws IOException, ClientException;

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
}
