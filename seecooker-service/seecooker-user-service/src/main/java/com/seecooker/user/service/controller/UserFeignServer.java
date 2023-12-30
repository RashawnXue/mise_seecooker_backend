package com.seecooker.user.service.controller;

import com.seecooker.common.core.model.Result;
import com.seecooker.common.core.model.dto.user.UserDTO;
import com.seecooker.user.service.service.UserClientService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * open feign api 控制器
 *
 * @author xueruichen
 * @date 2023.12.29
 */
@RestController
@RequestMapping
@Slf4j
public class UserFeignServer {
    private final UserClientService userService;

    public UserFeignServer(UserClientService userService) {
        this.userService = userService;
    }

    @GetMapping("feign/user")
    public Result<UserDTO> getUserById(@RequestParam @NotNull Long userId) {
        UserDTO user = userService.getUserById(userId);
        return Result.success(user);
    }

    @PutMapping("feign/user/update/postRecipes")
    public Result<Void> updatePostRecipes(@RequestParam Long userId, @RequestParam List<Long> recipes) {
        userService.updatePostRecipes(userId, recipes);
        return Result.success();
    }

    @PutMapping("feign/user/update/favorite")
    Result<Boolean> updateFavoriteRecipe(@RequestParam Long userId, @RequestParam Long recipeId){
        Boolean res = userService.updateFavoriteRecipe(userId, recipeId);
        return Result.success(res);
    }

    @PutMapping("feign/user/update/posts")
    Result<Void> updateUserPosts(@RequestParam Long userId, @RequestParam List<Long> posts) {
        userService.updateUserPosts(userId, posts);
        return Result.success();
    }
}
