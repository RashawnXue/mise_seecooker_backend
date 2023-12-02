package com.mise.seecooker.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.mise.seecooker.dao.RecipeDao;
import com.mise.seecooker.dao.UserDao;
import com.mise.seecooker.entity.po.RecipePO;
import com.mise.seecooker.entity.po.UserPO;
import com.mise.seecooker.entity.vo.recipe.PublishRecipeVO;
import com.mise.seecooker.entity.vo.recipe.RecipeDetailVO;
import com.mise.seecooker.entity.vo.recipe.RecipeVO;
import com.mise.seecooker.enums.ImageType;
import com.mise.seecooker.exception.BizException;
import com.mise.seecooker.exception.ErrorType;
import com.mise.seecooker.service.RecipeService;
import com.mise.seecooker.util.AliOSSUtil;
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
        return recipes.stream().sorted(Comparator.comparing(RecipePO::getCreateTime))
                .map(recipePO -> RecipeVO.builder()
                        .cover(recipePO.getCover())
                        .id(recipePO.getId())
                        .name(recipePO.getName())
                        .build())
                .toList();
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
        List<RecipePO> recipes = recipeDao.findByNameLike("%" + query + "%");
        return recipes.stream().sorted(Comparator.comparing(RecipePO::getCreateTime))
                .map(recipePO -> RecipeVO.builder()
                        .cover(recipePO.getCover())
                        .id(recipePO.getId())
                        .name(recipePO.getName())
                        .build())
                .toList();
    }
}
