package com.seecooker.controller.user;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.seecooker.pojo.vo.user.LoginVO;
import com.seecooker.pojo.vo.user.RegisterVO;
import com.seecooker.pojo.vo.user.UserInfoVO;
import com.seecooker.common.Result;
import com.seecooker.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
@RestController
@RequestMapping("/v1/")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     *
     * @param registerVO 用户账号和密码
     * @param avatar 头像文件
     * @return 响应结果
     */
    @PostMapping("user")
    public Result<Void> register(@Validated RegisterVO registerVO, MultipartFile avatar) throws Exception {
        String url = userService.uploadAvatar(avatar);
        userService.addUser(registerVO.getUsername(), registerVO.getPassword(), url);
        return Result.success();
    }

    /**
     * 用户登陆
     *
     * @param loginVO 登陆数据VO类
     * @return 响应结果
     */
    @PostMapping("session")
    public Result<SaTokenInfo> login(@Validated @RequestBody LoginVO loginVO) {
        userService.login(loginVO.getUsername(), loginVO.getPassword());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Result.success(tokenInfo);
    }

    /**
     * 退出登陆
     *
     * @return 响应结果
     */
    @DeleteMapping("session")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }

    /**
     * 获取当前登陆的用户信息
     *
     * @return 当前登陆的用户信息
     */
    @GetMapping("user")
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
    @GetMapping("user/{id}")
    public Result<UserInfoVO> getUserInfoById(@PathVariable Long id) {
        UserInfoVO userInfoVO = userService.getUserById(id);
        return Result.success(userInfoVO);
    }

    /**
     * 根据用户名修改用户名
     * 要求用户已登录
     * @param username 原用户名
     * @param newname  新用户名
     * @return 响应结果
     */
    @PostMapping("/modify/username")
    public Result<Void> modifyUsername(String username,String newname) {
        StpUtil.checkLogin();
        userService.modifyUsername(username,newname);
        return Result.success();
    }

    /**
     * 根据用户名修改密码
     * @param username 用户名
     * @param password 原密码
     * @param newPassword 新密码
     * @return 响应结果
     */
    @PostMapping("/modify/password")
    public Result<Void> modifyPassword(String username,String password,String newPassword){
        userService.modifyPassword(username,password,newPassword);
        return Result.success();
    }
    @PostMapping("/modify/avatar")
    public Result<Void> modifyAvatar(String username,MultipartFile avatar)throws Exception{
        StpUtil.checkLogin();
        String url = userService.uploadAvatar(avatar);
        userService.modifyAvatar(username,url);
        return Result.success();
    }
}
