package com.seecooker.recipe.service.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.seecooker.common.core.enums.ImageType;
import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;

import com.seecooker.common.core.model.Result;
import com.seecooker.common.core.model.dto.user.UserDTO;
import com.seecooker.feign.user.UserClient;
import com.seecooker.recipe.service.dao.IngredientAmountDao;
import com.seecooker.recipe.service.dao.IngredientDao;
import com.seecooker.recipe.service.dao.RecipeDao;
import com.seecooker.recipe.service.dao.RecipeScoreDao;
import com.seecooker.recipe.service.pojo.po.IngredientAmountPO;
import com.seecooker.recipe.service.pojo.po.IngredientPO;
import com.seecooker.recipe.service.pojo.po.RecipePO;
import com.seecooker.recipe.service.pojo.po.RecipeScorePO;
import com.seecooker.recipe.service.pojo.vo.PublishRecipeVO;
import com.seecooker.recipe.service.pojo.vo.RecipeDetailVO;
import com.seecooker.recipe.service.pojo.vo.RecipeListVO;
import com.seecooker.recipe.service.service.RecipeService;
import com.seecooker.util.oss.AliOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 菜谱业务服务层实现类
 *
 * @author xueruichen
 * @date 2023.11.27
 */
@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {
    private final IngredientAmountDao ingredientAmountDao;
    private final IngredientDao ingredientDao;
    private final RecipeScoreDao recipeScoreDao;
    private final RecipeDao recipeDao;
    private final UserClient userClient;

    public RecipeServiceImpl(RecipeDao recipeDao,
                             RecipeScoreDao recipeScoreDao,
                             IngredientDao ingredientDao,
                             IngredientAmountDao ingredientAmountDao, UserClient userClient) {
        this.recipeDao = recipeDao;
        this.recipeScoreDao = recipeScoreDao;
        this.ingredientDao = ingredientDao;
        this.ingredientAmountDao = ingredientAmountDao;
        this.userClient = userClient;
    }

    @Override
    public Long addRecipe(PublishRecipeVO publishRecipe, MultipartFile cover, MultipartFile[] stepImages) throws IOException, ClientException {
        RecipePO recipe = RecipePO.builder()
                .name(publishRecipe.getName())
                .introduction(publishRecipe.getIntroduction())
                .authorId(StpUtil.getLoginIdAsLong())
                .cover(AliOSSUtil.uploadFile(cover, ImageType.RECIPE_COVER_IMAGE))
                .stepImages(AliOSSUtil.uploadFile(stepImages, ImageType.RECIPE_STEP_IMAGE))
                .stepContents(publishRecipe.getStepContents())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .score(0.0)
                .build();
        recipe = recipeDao.save(recipe);

        Result<UserDTO> getUserResult = userClient.getUserById(StpUtil.getLoginIdAsLong());
        if (!getUserResult.isSuccess()) {
            throw new BizException(ErrorType.OPEN_FEIGN_API_ERROR);
        }
        UserDTO author = getUserResult.getData();
        author.getPostRecipes().add(recipe.getId());

        Result<Void> updateRecipesResult = userClient.updatePostRecipes(StpUtil.getLoginIdAsLong(), author.getPostRecipes());
        if (!updateRecipesResult.isSuccess()) {
            throw new BizException(ErrorType.OPEN_FEIGN_API_ERROR);
        }

        List<String> ingredients = publishRecipe.getIngredients();
        List<String> amounts = publishRecipe.getAmounts();
        for (int i = 0 ; i < ingredients.size() ; ++i) {
            IngredientPO ingredient = IngredientPO.builder().name(ingredients.get(i)).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build();
            ingredient = ingredientDao.save(ingredient);
            IngredientAmountPO ingredientAmount = IngredientAmountPO.builder()
                    .ingredientId(ingredient.getId())
                    .amount(amounts.get(i))
                    .recipeId(recipe.getId())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            ingredientAmountDao.save(ingredientAmount);
        }

        return recipe.getId();
    }

    @Override
    public List<RecipeListVO> getRecipes() {
        List<RecipePO> recipes = recipeDao.findAll();
        return mapRecipes(recipes);
    }

    @Override
    public RecipeDetailVO getRecipeDetailById(Long recipeId) {
        RecipePO recipe = recipeDao.findById(recipeId).get();

        boolean isFavorite = false;
        boolean isLogin = StpUtil.isLogin();

        Result<UserDTO> authorResult = userClient.getUserById(recipe.getAuthorId());
        if (!authorResult.isSuccess()) {
            throw new BizException(ErrorType.OPEN_FEIGN_API_ERROR);
        }
        UserDTO author = authorResult.getData();

        if (isLogin) {
            isFavorite = author.getFavoriteRecipes().contains(recipeId);
        }

        Map<String, String> ingredientAmount = new LinkedHashMap<>();
        List<IngredientAmountPO> ingredientAmountPOS = ingredientAmountDao.getIngredientAmountPOSByRecipeId(recipeId);
        for (IngredientAmountPO ingredientAmountPO : ingredientAmountPOS) {
            IngredientPO ingredientPO = ingredientDao.findById(ingredientAmountPO.getIngredientId()).get();
            ingredientAmount.put(ingredientPO.getName(),ingredientAmountPO.getAmount());
        }

        return RecipeDetailVO.builder()
                .authorAvatar(author.getAvatar())
                .authorName(author.getUsername())
                .introduction(recipe.getIntroduction())
                .stepContents(recipe.getStepContents())
                .stepImages(recipe.getStepImages())
                .name(recipe.getName())
                .cover(recipe.getCover())
                .isFavorite(isFavorite)
                .score(recipe.getScore())
                .ingredientAmounts(ingredientAmount)
                .build();
    }

    @Override
    public List<RecipeListVO> getRecipesByNameLike(String query) {
        List<RecipePO> recipes = recipeDao.findByNameLike("%" + String.join("%", query.split("")) + "%");
        return mapRecipes(recipes);
    }

    @Override
    public Boolean favoriteRecipe(Long recipeId) {
        Long userId = StpUtil.getLoginIdAsLong();
        Result<Boolean> result = userClient.updateFavoriteRecipe(userId, recipeId);
        if (!result.isSuccess()) {
            throw new BizException(ErrorType.OPEN_FEIGN_API_ERROR);
        }
        return result.getData();
    }

    @Override
    public double scoreRecipe(Long recipeId, Double score) {
        Long userId = StpUtil.getLoginIdAsLong();
        RecipeScorePO recipeScore = recipeScoreDao.findRecipeScorePOByUserIdAndRecipeId(userId, recipeId);
        if (recipeScore != null) {
            throw new BizException(ErrorType.RECIPE_ALREADY_SCORED, "用户已对该菜谱评分");
        }
        recipeScore = RecipeScorePO.builder().recipeId(recipeId).userId(userId).score(score)
                .createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build();
        recipeScoreDao.save(recipeScore);
        RecipePO recipe = recipeDao.findById(recipeId).get();
        List<RecipeScorePO> recipeScorePOS = recipeScoreDao.findRecipeScorePOSByRecipeId(recipeId);
        double averageScore = recipeScorePOS.stream().mapToDouble(RecipeScorePO::getScore).average().getAsDouble();
        recipe.setScore(averageScore);
        recipeDao.save(recipe);
        return averageScore;
    }

    private List<RecipeListVO> mapRecipes(List<RecipePO> recipes) {
        boolean isLogin = StpUtil.isLogin();
        List<Long> favoriteRecipes;
        if (isLogin) {
            Long userId = StpUtil.getLoginIdAsLong();
            Result<UserDTO> userResult = userClient.getUserById(userId);
            if (!userResult.isSuccess()) {
                throw new BizException(ErrorType.OPEN_FEIGN_API_ERROR);
            }
            UserDTO user = userResult.getData();
            favoriteRecipes = user.getFavoriteRecipes();
        } else {
            favoriteRecipes = Collections.emptyList();
        }
        return recipes.stream().sorted(Comparator.comparing(RecipePO::getCreateTime))
                .map(recipePO -> {
                    Result<UserDTO> authorResult = userClient.getUserById(recipePO.getAuthorId());
                    if (!authorResult.isSuccess()) {
                        throw new BizException(ErrorType.OPEN_FEIGN_API_ERROR);
                    }
                    UserDTO author = authorResult.getData();
                    boolean isFavorite = false;
                    if (isLogin) {
                        isFavorite = favoriteRecipes.contains(recipePO.getId());
                    }
                    return RecipeListVO.builder()
                            .cover(recipePO.getCover())
                            .id(recipePO.getId())
                            .name(recipePO.getName())
                            .introduction(recipePO.getIntroduction())
                            .score(recipePO.getScore())
                            .authorAvatar(author.getAvatar())
                            .authorName(author.getUsername())
                            .isFavorite(isFavorite)
                            .build();
                })
                .toList();
    }
}
