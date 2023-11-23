package com.mise.seecooker.controller.user;

import com.mise.seecooker.entity.Result;
import com.mise.seecooker.entity.vo.LoginVO;
import com.mise.seecooker.entity.vo.user.UserInfoVO;
import com.mise.seecooker.service.UserService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户业务控制层
 *
 * @author xueruichen
 * @date 2023.11.17
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @param avatar 头像文件
     * @return 响应结果
     */
    @PostMapping("/user")
    public Result<?> register(@RequestParam(value = "username") String username,
                              @RequestParam(value = "password") String password,
                              @RequestParam(value = "avatar") MultipartFile avatar) throws Exception {
        String url = userService.uploadAvatar(avatar);
        userService.addUser(username, password, url);
        return Result.success();
    }

    /**
     * 用户登陆
     *
     * @param loginVO 登陆数据VO类
     * @return 响应结果
     */
    @PostMapping("/session")
    public Result<?> login(@Validated @RequestBody LoginVO loginVO) {
        userService.login(loginVO.getUsername(), loginVO.getPassword());
        return Result.success();
    }

    /**
     * 退出登陆
     *
     * @return 响应结果
     */
    @DeleteMapping("/session")
    public Result<?> logout() {
        userService.logout();
        return Result.success();
    }

    /**
     * 获取当前登陆的用户信息
     *
     * @return 当前登陆的用户信息
     */
    @GetMapping("/user")
    public Result<UserInfoVO> getCurrentLoginUser() {
        UserInfoVO user = userService.getCurrentLoginUser();
        return Result.success(user);
    }

    /**
     * 根据用户id获取用户信息
     *
     * @param id 用户id
     * @return 用户信息
     */
    @GetMapping("/user/{id}")
    public Result<UserInfoVO> getUserInfoById(@PathVariable Long id) {
        UserInfoVO userInfoVO = userService.getUserById(id);
        return Result.success(userInfoVO);
    }

}
