package com.seecooker.pojo.vo.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 用户登陆VO类
 *
 * @author xueruichen
 * @date 2023.11.23
 */
@Getter
public class LoginVO {
    /**
     * 用户名
     */
    @NotNull
    private String username;

    /**
     * 用户密码
     */
    @NotNull
    private String password;
}
