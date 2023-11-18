package com.mise.seecooker.controller.user;

import com.mise.seecooker.entity.Result;
import com.mise.seecooker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户业务控制层
 *
 * @author xueruichen
 * @date 2023.11.17
 */
@Slf4j
@RestController
@RequestMapping("/v1")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<?> register() {
        return Result.success();
    }

}
