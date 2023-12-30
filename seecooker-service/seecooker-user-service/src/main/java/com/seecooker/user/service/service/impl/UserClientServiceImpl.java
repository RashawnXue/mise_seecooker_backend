package com.seecooker.user.service.service.impl;

import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;
import com.seecooker.common.core.model.dto.user.UserDTO;
import com.seecooker.user.service.dao.UserDao;
import com.seecooker.user.service.pojo.po.UserPO;
import com.seecooker.user.service.service.UserClientService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * feign-api 用户服务实现类
 *
 * @author xueruichen
 * @date 2023.12.29
 */
@Service
@Slf4j
@Transactional
public class UserClientServiceImpl implements UserClientService {
    private final UserDao userDao;

    public UserClientServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDTO getUserById(Long id) {
        Optional<UserPO> userOp = userDao.findById(id);
        if (userOp.isEmpty()) {
            throw new BizException(ErrorType.USER_NOT_EXIST, "用户不存在");
        }
        UserPO user = userOp.get();

        return UserDTO.builder()
                .id(id)
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .posts(user.getPosts())
                .postRecipes(user.getPostRecipes())
                .favoriteRecipes(user.getFavoriteRecipes())
                .posts(user.getPosts())
                .signature(user.getSignature())
                .build();
    }

    @Override
    public void updatePostRecipes(Long userId, List<Long> recipes) {
        UserPO user = getUser(userId);
        user.setPostRecipes(recipes);
        userDao.save(user);
    }

    @Override
    public Boolean updateFavoriteRecipe(Long userId, Long recipeId) {
        UserPO user = getUser(userId);
        boolean favorite = false;
        if (user.getFavoriteRecipes().contains(recipeId)) {
            user.getFavoriteRecipes().remove(recipeId);
        } else {
            user.getFavoriteRecipes().add(recipeId);
            favorite = true;
        }
        userDao.save(user);
        return favorite;
    }

    @Override
    public void updateUserPosts(Long userId, List<Long> posts) {
        UserPO user = getUser(userId);
        user.setPosts(posts);
        userDao.save(user);
    }

    private UserPO getUser(Long id) {
        Optional<UserPO> userOp = userDao.findById(id);
        if (userOp.isEmpty()) {
            throw new BizException(ErrorType.USER_NOT_EXIST, "用户不存在");
        }
        return userOp.get();
    }
}
