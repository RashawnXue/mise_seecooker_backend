package com.mise.seecooker.service;

import com.aliyuncs.exceptions.ClientException;
import com.mise.seecooker.entity.vo.recipe.PublishRecipeVO;
import com.mise.seecooker.entity.vo.recipe.RecipeDetailVO;
import com.mise.seecooker.entity.vo.recipe.RecipeVO;
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
    List<RecipeVO> getRecipes();

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
    List<RecipeVO> getRecipesByNameLike(String query);
}
