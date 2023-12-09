package com.seecooker.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.seecooker.pojo.po.RecipePO;
import com.seecooker.pojo.po.UserPO;
import com.seecooker.pojo.vo.recipe.PublishRecipeVO;
import com.seecooker.pojo.vo.recipe.RecipeDetailVO;
import com.seecooker.pojo.vo.recipe.RecipeVO;
import com.seecooker.common.core.enums.ImageType;
import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;
import com.seecooker.dao.RecipeDao;
import com.seecooker.dao.UserDao;
import com.seecooker.oss.util.AliOSSUtil;
import com.seecooker.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

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

    public RecipeServiceImpl(RecipeDao recipeDao, UserDao userDao) {
        this.recipeDao = recipeDao;
        this.userDao = userDao;
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
    public List<RecipeVO> getRecipes() {
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
        return RecipeDetailVO.builder()
                .authorAvatar(author.getAvatar())
                .authorName(author.getUsername())
                .introduction(recipe.getIntroduction())
                .stepContents(recipe.getStepContents())
                .stepImages(recipe.getStepImages())
                .name(recipe.getName())
                .cover(recipe.getCover())
                .build();
    }

    @Override
    public List<RecipeVO> getRecipesByNameLike(String query) {
        List<RecipePO> recipes = recipeDao.findByNameLike("%" + String.join("%", query.split("")) + "%");
        return mapRecipes(recipes);
    }

    private List<RecipeVO> mapRecipes(List<RecipePO> recipes) {
        return recipes.stream().sorted(Comparator.comparing(RecipePO::getCreateTime))
                .map(recipePO -> {
                    UserPO author = userDao.findById(recipePO.getAuthorId()).get();
                    return RecipeVO.builder()
                            .cover(recipePO.getCover())
                            .id(recipePO.getId())
                            .name(recipePO.getName())
                            .authorAvatar(author.getAvatar())
                            .authorName(author.getUsername())
                            .build();
                })
                .toList();
    }
}
