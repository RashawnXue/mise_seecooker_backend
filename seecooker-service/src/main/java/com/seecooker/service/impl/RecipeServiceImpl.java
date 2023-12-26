package com.seecooker.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.seecooker.common.redis.enums.RedisKey;
import com.seecooker.pojo.po.RecipePO;
import com.seecooker.pojo.po.UserPO;
import com.seecooker.pojo.vo.recipe.PublishRecipeVO;
import com.seecooker.pojo.vo.recipe.RecipeDetailVO;
import com.seecooker.pojo.vo.recipe.RecipeListVO;
import com.seecooker.common.core.enums.ImageType;
import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;
import com.seecooker.dao.RecipeDao;
import com.seecooker.dao.UserDao;
import com.seecooker.oss.util.AliOSSUtil;
import com.seecooker.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RecipeDao recipeDao;
    private final UserDao userDao;
    private final RedisTemplate redisTemplate;

    public RecipeServiceImpl(RecipeDao recipeDao, UserDao userDao, RedisTemplate redisTemplate) {
        this.recipeDao = recipeDao;
        this.userDao = userDao;
        this.redisTemplate = redisTemplate;
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
                .build();
        recipe = recipeDao.save(recipe);
        UserPO author = userDao.findById(StpUtil.getLoginIdAsLong()).get();
        author.getPostRecipes().add(recipe.getId());
        userDao.save(author);
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
        // 判断用户是否存在
        if (!userDao.existsById(recipe.getAuthorId())) {
            throw new BizException(ErrorType.USER_NOT_EXIST);
        }
        UserPO author = userDao.findById(recipe.getAuthorId()).get();

        boolean isFavorite = false;
        boolean isLogin = StpUtil.isLogin();

        if (isLogin) {
            // 先查缓存
            long userId = StpUtil.getLoginIdAsLong();
            String key = RedisKey.RECIPE_FAVORITE.getKey() + RedisKey.RECIPE_FAVORITE_DELIMITER.getKey() + userId;
            String hashKey = recipeId.toString();
            Boolean hashResult = (Boolean) redisTemplate.opsForHash().get(key, hashKey);

            if (Boolean.FALSE.equals(hashResult)) {
                // 若缓存中为false，则为false
                isFavorite = false;
            }
            else if (Boolean.TRUE.equals(hashResult) || author.getFavoriteRecipes().contains(recipeId)) {
                // 若缓存中或数据库中有收藏，则为true
                isFavorite = true;
                redisTemplate.opsForHash().put(key, hashKey, Boolean.TRUE);
            }
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
                .build();
    }

    @Override
    public List<RecipeListVO> getRecipesByNameLike(String query) {
        List<RecipePO> recipes = recipeDao.findByNameLike("%" + String.join("%", query.split("")) + "%");
        return mapRecipes(recipes);
    }

    @Override
    public Boolean favoriteRecipe(Long recipeId) {
        long userId = StpUtil.getLoginIdAsLong();
        // 在redis中的hash进行记录，结构：key--RECIPE_FAVORITE::userId, hashKey--recipeId, value--Boolean
        String key = RedisKey.RECIPE_FAVORITE.getKey() + RedisKey.RECIPE_FAVORITE_DELIMITER.getKey() + userId;
        String hashKey = Long.toString(recipeId);
        // 获取对应记录, value表示当前用户是否已经收藏
        Boolean value = (Boolean) redisTemplate.opsForHash().get(key, hashKey);

        // 无记录，读取数据库
        if (value == null) {
            Optional<UserPO> userOptional = userDao.findById(userId);
            if (userOptional.isEmpty()) {
                throw new BizException(ErrorType.USER_NOT_EXIST);
            }
            value = userOptional.get().getFavoriteRecipes().contains(recipeId);
        }

        if (Boolean.TRUE.equals(value)) {
            // 已收藏，删除记录
            redisTemplate.opsForHash().put(key, hashKey, Boolean.FALSE);
        } else {
            // 未收藏，添加记录
            redisTemplate.opsForHash().put(key, hashKey, Boolean.TRUE);
        }

        return Boolean.FALSE.equals(value);
    }

    private List<RecipeListVO> mapRecipes(List<RecipePO> recipes) {
        boolean isLogin = StpUtil.isLogin();
        List<Long> favoriteRecipes;
        if (isLogin) {
            Long userId = StpUtil.getLoginIdAsLong();
            UserPO user = userDao.findById(userId).get();
            favoriteRecipes = user.getFavoriteRecipes();
        } else {
            favoriteRecipes = Collections.emptyList();
        }
        return recipes.stream().sorted(Comparator.comparing(RecipePO::getCreateTime))
                .map(recipePO -> {
                    UserPO author = userDao.findById(recipePO.getAuthorId()).get();
                    boolean isFavorite = false;
                    if (isLogin) {
                        isFavorite = favoriteRecipes.contains(recipePO.getId());
                    }
                    return RecipeListVO.builder()
                            .cover(recipePO.getCover())
                            .id(recipePO.getId())
                            .name(recipePO.getName())
                            .authorAvatar(author.getAvatar())
                            .authorName(author.getUsername())
                            .isFavorite(isFavorite)
                            .build();
                })
                .toList();
    }
}
