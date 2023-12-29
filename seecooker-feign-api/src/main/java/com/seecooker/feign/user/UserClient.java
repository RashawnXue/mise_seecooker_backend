package com.seecooker.feign.user;

import com.seecooker.common.core.model.Result;
import com.seecooker.common.core.model.dto.user.UserDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "seecooker-user-service")
public interface UserClient {
    @GetMapping("feign/user")
    Result<UserDTO> getUserById(@RequestParam @NotNull Long userId);

    @PutMapping("feign/user/update/postRecipes")
    Result<Void> updatePostRecipes(@RequestParam Long userId, @RequestParam List<Long> recipes);

    @PutMapping("feign/user/update/favorite")
    Result<Boolean> updateFavoriteRecipe(@RequestParam Long userId, @RequestParam Long recipeId);
}