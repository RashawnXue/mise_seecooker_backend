package com.mise.seecooker.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.github.javafaker.Faker;
import com.mise.seecooker.dao.RecipeDao;
import com.mise.seecooker.dao.UserDao;
import com.mise.seecooker.entity.po.UserPO;
import com.mise.seecooker.entity.vo.recipe.PublishRecipeVO;
import com.mise.seecooker.service.RecipeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Locale;

@SpringBootTest
public class RecipeServiceImplTest {
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private RecipeDao recipeDao;
    @Autowired
    private UserDao userDao;

    private final Faker faker = new Faker(Locale.CHINA);

    @BeforeEach
    void registerAndLogin() {
        String username = "testUser222";
        String password = "12345678abc";
        Long id = userDao.save(UserPO.builder()
                .username(username)
                .password(password)
                .posts(List.of())
                .build()).getId();
        StpUtil.login(id);
    }

    @AfterEach
    void clear() {
        recipeDao.deleteAll();
        StpUtil.logout();
        userDao.deleteAll();
    }

    @Test
    void addRecipeTest() {
        PublishRecipeVO publishRecipe = PublishRecipeVO.builder()
                .name(faker.app().name())
                .introduction(faker.name().title())
                .stepContents(List.of("step1", "step2", "step3"))
                .build();
    }
}
