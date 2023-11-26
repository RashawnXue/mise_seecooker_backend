package com.mise.seecooker.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.mise.seecooker.dao.UserDao;
import com.mise.seecooker.entity.po.UserPO;
import com.mise.seecooker.entity.vo.user.UserInfoVO;
import com.mise.seecooker.enums.ImageType;
import com.mise.seecooker.exception.BizException;
import com.mise.seecooker.exception.ErrorType;
import com.mise.seecooker.service.UserService;
import com.mise.seecooker.util.AliOSSUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户业务服务层实现
 *
 * @author xueruichen
 * @date 2023.11.17
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Long addUser(String username, String password, String avatar) {
        UserPO user = userDao.findByUsername(username);
        // 用户已存在，抛出异常
        if (user != null) {
            log.error("The user already exists");
            throw new BizException(ErrorType.USER_ALREADY_EXIST);
        }
        user = userDao.save(UserPO.builder()
                        .username(username)
                        .password(BCrypt.hashpw(password))
                        .avatar(avatar)
                        .posts(List.of())
                        .postRecipes(List.of())
                        .likeRecipes(List.of())
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build());
        return user.getId();
    }

    @Override
    public void login(String username, String password) {
        UserPO user = userDao.findByUsername(username);
        // 用户名不存在
        if (user == null) {
            log.error("The username does not exist");
            throw new BizException(ErrorType.USER_NOT_EXIST);
        }
        // 密码错误
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BizException(ErrorType.PASSWORD_ERROR);
        }
        StpUtil.login(user.getId());
    }

    @Override
    public UserInfoVO getUserById(Long id) {
        return getUserInfoById(id);
    }

    @Override
    public void logout() {
        // 检查用户是否登陆
        StpUtil.checkLogin();
        // 若已登陆，则登出
        StpUtil.logout();
    }

    @Override
    public UserInfoVO getCurrentLoginUser() {
        return getUserInfoById(getCurrentLoginUserId());
    }

    @Override
    public String uploadAvatar(MultipartFile avatar) throws IOException, ClientException {
        if (avatar == null) {
            return null;
        }
        return AliOSSUtil.uploadFile(avatar, ImageType.AVATAR);
    }

    private UserInfoVO getUserInfoById(Long id) {
        Optional<UserPO> user = userDao.findById(id);
        if (user.isEmpty()) {
            log.error("The user dos not exist");
            throw new BizException(ErrorType.USER_NOT_EXIST);
        }
        return UserInfoVO.builder()
                .username(user.get().getUsername())
                .avatar(user.get().getAvatar())
                .build();
    }

    @Override
    public Long getCurrentLoginUserId() {
        StpUtil.checkLogin();
        return StpUtil.getLoginIdAsLong();
    }
}
