package com.mise.seecooker.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.github.javafaker.Faker;
import com.mise.seecooker.dao.UserDao;
import com.mise.seecooker.entity.po.UserPO;
import com.mise.seecooker.entity.vo.user.UserInfoVO;
import com.mise.seecooker.exception.BizException;
import com.mise.seecooker.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 用户服务单元测试
 *
 * @author xueruichen
 * @date 2023.11.23
 */
@SpringBootTest
public class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;

    private final Faker faker = new Faker(Locale.CHINA);

    @AfterEach
    void clear() {
        userDao.deleteAll();
    }

    @Test
    void addUserTest() {
        String username1 = faker.name().username();
        String username2 = faker.name().username();
        String username3 = faker.name().username();
        Long id1 = userService.addUser(username1, "12345678abc", faker.avatar().image());
        Long id2 = userService.addUser(username2, "12345678efg", faker.avatar().image());
        Long id3 = userService.addUser(username3, "12345678hij", faker.avatar().image());
        assertEquals(username1, userDao.findById(id1).get().getUsername());
        assertEquals(username2, userDao.findById(id2).get().getUsername());
        assertEquals(username3, userDao.findById(id3).get().getUsername());

        assertThrows(BizException.class, ()->userService.addUser(username1, "12345678abc", faker.avatar().image()));
    }

    @Test
    void loginTest() {
        String username = faker.name().username();
        String password = "12345678abc";
        userDao.save(UserPO.builder()
                .username(username)
                .password(BCrypt.hashpw(password))
                .build());
        // 正常登陆
        userService.login(username, password);
        // 用户不存在
        assertThrows(BizException.class, ()->userService.login(username+123, password));
        // 密码错误
        assertThrows(BizException.class, ()->userService.login(username, password+123));
    }

    @Test
    void getUserByIdTest() throws ClientException {
        String username = faker.name().username();
        String password = "12345678abc";
        Long id = userDao.save(UserPO.builder()
                .username(username)
                .password(BCrypt.hashpw(password))
                .avatar(null)
                .build()).getId();
        UserInfoVO user = userService.getUserById(id);
        assertEquals(username, user.getUsername());
        // 用户id不存在，抛出异常
        assertThrows(BizException.class, ()->userService.getUserById(id+1));
    }

    @Test
    void getCurrentLoginUserIdTest() {
        String username = faker.name().username();
        String password = "12345678abc";
        Long id = userDao.save(UserPO.builder()
                .username(username)
                .password(BCrypt.hashpw(password))
                .build()).getId();
        // 未登陆
        assertThrows(NotLoginException.class, ()->userService.getCurrentLoginUserId());
        StpUtil.login(id);
        Long loginId = userService.getCurrentLoginUserId();
        assertEquals(id, loginId);
        StpUtil.logout();
    }

    @Test
    void getCurrentLoginUserTest() {
        String username = faker.name().username();
        String password = "12345678abc";
        Long id = userDao.save(UserPO.builder()
                .username(username)
                .password(BCrypt.hashpw(password))
                .build()).getId();
        StpUtil.login(id);
        UserInfoVO user = userService.getCurrentLoginUser();
        assertEquals(username, user.getUsername());
        StpUtil.logout();
    }
}
