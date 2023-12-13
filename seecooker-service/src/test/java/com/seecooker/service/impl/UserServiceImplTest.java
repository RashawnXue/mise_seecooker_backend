package com.seecooker.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.github.javafaker.Faker;
import com.seecooker.common.core.exception.BizException;
import com.seecooker.dao.UserDao;
import com.seecooker.pojo.po.UserPO;
import com.seecooker.pojo.vo.user.UserInfoVO;
import com.seecooker.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

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
    void getUserByIdTest() {
        String username = faker.name().username();
        String password = "12345678abc";
        Long id = userDao.save(UserPO.builder()
                .username(username)
                .password(BCrypt.hashpw(password))
                .avatar(null)
                .posts(Collections.emptyList())
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
                .posts(Collections.emptyList())
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
                .posts(Collections.emptyList())
                .password(BCrypt.hashpw(password))
                .build()).getId();
        StpUtil.login(id);
        UserInfoVO user = userService.getCurrentLoginUser();
        assertEquals(username, user.getUsername());
        StpUtil.logout();
    }
    @Test
    void modifyUsernameTest() {
        String username = faker.name().username();
        String password = "12345678abc";
        Long id = userDao.save(UserPO.builder()
                .username(username)
                .posts(Collections.emptyList())
                .password(BCrypt.hashpw(password))
                .build()).getId();
        StpUtil.login(id);
        String newUsername = faker.name().username();
        //用户名或新用户名为空
        assertThrows(BizException.class, ()->userService.modifyUsername(username, ""));
        assertThrows(BizException.class, ()->userService.modifyUsername(null, newUsername));
        //用户名不存在
        assertThrows(BizException.class, ()->userService.modifyUsername("not_exist", newUsername));
        userService.modifyUsername(username, newUsername);
        assertEquals(newUsername, userDao.findById(id).get().getUsername());
        StpUtil.logout();
    }
    @Test
    void modifyPasswordTest(){
        String username = faker.name().username();
        String password = "12345678abc";
        String hashedPassword = BCrypt.hashpw(password);
        Long id = userDao.save(UserPO.builder()
                .username(username)
                .posts(Collections.emptyList())
                .password(hashedPassword)
                .build()).getId();
        String newPassword = "87654321cba";
        //原密码错误
        assertThrows(BizException.class, ()->userService.modifyPassword(username, "1", newPassword));
        //用户名不存在
        assertThrows(BizException.class, ()->userService.modifyPassword("not_exist", password, password));
        userService.modifyPassword(username, password, newPassword);
        assertTrue(BCrypt.checkpw(newPassword, userDao.findById(id).get().getPassword()));
    }
    @Test
    void modifyAvatarTest(){
        String username = faker.name().username();
        String password = "12345678abc";
        String avatar = faker.avatar().image();
        Long id = userDao.save(UserPO.builder()
                .username(username)
                .posts(Collections.emptyList())
                .password(BCrypt.hashpw(password))
                .avatar(avatar)
                .build()).getId();
        String newAvatar = faker.avatar().image();
        userService.modifyAvatar(username, newAvatar);
        //用户名不存在
        assertThrows(BizException.class, ()->userService.modifyAvatar("not_exist", newAvatar));
        assertEquals(newAvatar, userDao.findById(id).get().getAvatar());
    }
    @Test
    void modifyAvatarTestNull(){
        String username = faker.name().username();
        String password = "12345678abc";
        String avatar = faker.avatar().image();
        Long id = userDao.save(UserPO.builder()
                .username(username)
                .posts(Collections.emptyList())
                .password(BCrypt.hashpw(password))
                .avatar(avatar)
                .build()).getId();
        userService.modifyAvatar(username, null);
        assertNull(userDao.findById(id).get().getAvatar());
    }
}
