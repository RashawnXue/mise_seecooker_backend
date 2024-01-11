package com.seecooker.recipe.service.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.seecooker.common.core.enums.ImageType;
import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;
import com.seecooker.common.core.model.Result;
import com.seecooker.common.core.model.dto.user.UserDTO;
import com.seecooker.common.redis.enums.RedisKey;
import com.seecooker.feign.user.UserClient;
import com.seecooker.recipe.service.dao.IngredientDao;
import com.seecooker.recipe.service.dao.RecipeDao;
import com.seecooker.recipe.service.dao.RecipeScoreDao;
import com.seecooker.recipe.service.pojo.po.IngredientPO;
import com.seecooker.recipe.service.pojo.po.RecipePO;
import com.seecooker.recipe.service.pojo.po.RecipeScorePO;
import com.seecooker.recipe.service.pojo.vo.*;
import com.seecooker.recipe.service.service.RecipeService;
import com.seecooker.util.oss.AliOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 菜谱业务服务层实现类
 *
 * @author xueruichen
 * @date 2023.11.27
 */
@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {
    private final IngredientDao ingredientDao;
    private final RecipeScoreDao recipeScoreDao;
    private final RecipeDao recipeDao;
    private final UserClient userClient;
    private final RedisTemplate redisTemplate;
    private static final int PAGE_SIZE = 8;

    public RecipeServiceImpl(RecipeDao recipeDao,
                             RecipeScoreDao recipeScoreDao,
                             IngredientDao ingredientDao,
                             UserClient userClient, RedisTemplate redisTemplate) {
        this.recipeDao = recipeDao;
        this.recipeScoreDao = recipeScoreDao;
        this.ingredientDao = ingredientDao;
        this.userClient = userClient;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void addRecipe(PublishRecipeVO publishRecipe, MultipartFile cover, MultipartFile[] stepImages) throws IOException, ClientException {
        RecipePO recipe = RecipePO.builder()
                .name(publishRecipe.getName())
                .introduction(publishRecipe.getIntroduction())
                .authorId(StpUtil.getLoginIdAsLong())
                .cover(AliOSSUtil.uploadFile(cover, ImageType.RECIPE_COVER_IMAGE))
                .stepImages(AliOSSUtil.uploadFile(stepImages, ImageType.RECIPE_STEP_IMAGE))
                .stepContents(publishRecipe.getStepContents())
                .ingredientList(publishRecipe.getIngredients())
                .amountList(publishRecipe.getAmounts())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .score(0.0)
                .favoriteNum(0)
                .build();
        recipe = recipeDao.save(recipe);

        UserDTO author = getUser(StpUtil.getLoginIdAsLong());
        author.getPostRecipes().add(recipe.getId());

        Result<Void> updateRecipesResult = userClient.updatePostRecipes(StpUtil.getLoginIdAsLong(), author.getPostRecipes());
        if (updateRecipesResult.fail()) {
            throw new BizException(ErrorType.OPEN_FEIGN_API_ERROR);
        }
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
        boolean isScored = false;
        double score = 0.0;
        boolean isLogin = StpUtil.isLogin();

        UserDTO author = getUser(recipe.getAuthorId());

        if (isLogin) {
            UserDTO user = getUser(StpUtil.getLoginIdAsLong());
            isFavorite = user.getFavoriteRecipes().contains(recipeId);
            RecipeScorePO recipeScore = recipeScoreDao.findRecipeScorePOByUserIdAndRecipeId(StpUtil.getLoginIdAsLong(), recipeId);
            if (recipeScore != null) {
                isScored = true;
                score = recipeScore.getScore();
            }
        }

        Map<String, String> ingredientAmount = new LinkedHashMap<>();
        for (int i = 0 ; i < recipe.getIngredientList().size() ; ++i) {
            ingredientAmount.put(recipe.getIngredientList().get(i), recipe.getAmountList().get(i));
        }

        return RecipeDetailVO.builder()
                .authorId(author.getId())
                .authorAvatar(author.getAvatar())
                .authorName(author.getUsername())
                .introduction(recipe.getIntroduction())
                .stepContents(recipe.getStepContents())
                .stepImages(recipe.getStepImages())
                .name(recipe.getName())
                .cover(recipe.getCover())
                .favorite(isFavorite)
                .averageScore(recipe.getScore())
                .score(score)
                .scored(isScored)
                .ingredientAmounts(ingredientAmount)
                .publishTime(recipe.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
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
        if (result.fail()) {
            throw new BizException(ErrorType.OPEN_FEIGN_API_ERROR);
        }
        Optional<RecipePO> recipeOp = recipeDao.findById(recipeId);
        if (recipeOp.isEmpty()) {
            throw new BizException(ErrorType.RECIPE_NOT_EXIST);
        }
        RecipePO recipe = recipeOp.get();
        // 更新收藏数据
        recipe.setFavoriteNum(recipe.getFavoriteNum() + (result.getData() ? 1 : -1));
        recipeDao.save(recipe);
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
        List<RecipeScorePO> recipeScorePOList = recipeScoreDao.findRecipeScorePOSByRecipeId(recipeId);
        double averageScore = recipeScorePOList.stream().mapToDouble(RecipeScorePO::getScore).average().getAsDouble();
        recipe.setScore(averageScore);
        recipeDao.save(recipe);
        return averageScore;
    }

    @Override
    public List<RecipeListVO> getRecipesByPage(Integer pageNo) {
        List<RecipePO> recipes = recipeDao.findAll(PageRequest.of(pageNo, PAGE_SIZE)).stream().toList();
        return mapRecipes(recipes);
    }

    @Override
    public List<RecipeListVO> getFavoriteRecipes(Long userId) {
        UserDTO user = getUser(userId);
        List<RecipePO> recipes = recipeDao.findAllById(user.getFavoriteRecipes());
        return mapRecipes(recipes);
    }

    @Override
    public List<String> getRandomRecipeName() {
        return recipeDao.getRandomName();
    }

    @Override
    public List<ExploreVO> getRecipesByIngredient(List<String> ingredients) {
        List<RecipePO> recipes = recipeDao.findAll();
        Set<String> ingredientSet = new LinkedHashSet<>(ingredients);
        List<ExploreVO> result = new ArrayList<>();
        for (RecipePO recipe : recipes) {
            int cnt = 0;
            boolean favorite = false;
            for (String ingredient : recipe.getIngredientList()) {
                if (ingredientSet.contains(ingredient)) {
                    cnt++;
                }
            }
            if (StpUtil.isLogin()) {
                UserDTO user = getUser(StpUtil.getLoginIdAsLong());
                favorite = user.getFavoriteRecipes().contains(recipe.getId());
            }
            if (cnt == ingredientSet.size()) {
                UserDTO author = getUser(recipe.getAuthorId());
                result.add(ExploreVO.builder()
                                .recipeId(recipe.getId())
                                .name(recipe.getName())
                                .authorAvatar(author.getAvatar())
                                .authorName(author.getUsername())
                                .introduction(recipe.getIntroduction())
                                .favorite(favorite)
                                .cover(recipe.getCover())
                                .build());
            }
        }
        return result;
    }

    @Override
    public List<IngredientVO> getIngredients() {
        Map<String, IngredientVO> map = new LinkedHashMap<>();
        if (redisTemplate.hasKey(RedisKey.INGREDIENT.getKey())) {
            map = (Map<String, IngredientVO>) redisTemplate.opsForValue().get(RedisKey.INGREDIENT.getKey());
        } else {
            List<IngredientPO> ingredients = ingredientDao.findAll();
            for (IngredientPO ingredient : ingredients) {
                if (map.containsKey(ingredient.getCategory())) {
                    IngredientVO names = map.get(ingredient.getCategory());
                    names.getName().add(ingredient.getName());
                } else {
                    map.put(ingredient.getCategory(), IngredientVO.builder()
                            .category(ingredient.getCategory())
                            .name(new ArrayList<>(Arrays.asList(ingredient.getName())))
                            .build());
                }
            }
            redisTemplate.opsForValue().set(RedisKey.INGREDIENT.getKey(), map, 1000*60*60L, TimeUnit.MILLISECONDS);
        }
        return new ArrayList<>(map.values());
    }

    @Override
    public List<RecipeListVO> getPublishRecipe(Long userId) {
        UserDTO user = getUser(userId);
        List<RecipePO> recipe = recipeDao.findAllById(user.getPostRecipes());
        return mapRecipes(recipe);
    }

    private List<RecipeListVO> mapRecipes(List<RecipePO> recipes) {
        boolean isLogin = StpUtil.isLogin();
        List<Long> favoriteRecipes;
        if (isLogin) {
            Long userId = StpUtil.getLoginIdAsLong();
            UserDTO user = getUser(userId);
            favoriteRecipes = user.getFavoriteRecipes();
        } else {
            favoriteRecipes = Collections.emptyList();
        }
        return recipes.stream()
                .map(recipePO -> {
                    UserDTO author = getUser(recipePO.getAuthorId());
                    boolean isFavorite = false;
                    if (isLogin) {
                        isFavorite = favoriteRecipes.contains(recipePO.getId());
                    }
                    return RecipeListVO.builder()
                            .cover(recipePO.getCover())
                            .recipeId(recipePO.getId())
                            .name(recipePO.getName())
                            .introduction(recipePO.getIntroduction())
                            .score(recipePO.getScore())
                            .authorId(author.getId())
                            .authorAvatar(author.getAvatar())
                            .authorName(author.getUsername())
                            .favorite(isFavorite)
                            .favoriteNum(recipePO.getFavoriteNum())
                            .publishTime(recipePO.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                            .build();
                })
                .toList();
    }

    private UserDTO getUser(Long userId) {
        Result<UserDTO> userResult = userClient.getUserById(userId);
        if (userResult.fail()) {
            throw new BizException(ErrorType.OPEN_FEIGN_API_ERROR);
        }
        return userResult.getData();
    }
}
