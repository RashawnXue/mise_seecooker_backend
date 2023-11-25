package com.mise.seecooker.entity.vo.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户注册VO类
 *
 * @author xueruichen
 * @date 2023.11.23
 */
@Getter
@Setter
public class RegisterVO {
    /**
     * 用户名
     */
    @NotNull
    @Size(min = 1, max = 16, message = "用户名长度必须在 1-16 之间")
    private String username;

    /**
     * 密码
     */
    @NotNull
    @Size(min = 6, max = 56, message = "密码长度必须在 8-56 之间")
    @Pattern.List({
            @Pattern(regexp = "^[\\x21-\\x7e]*$", message = "密码只能包含字母,数字和符号"),
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).*$", message = "密码未达到复杂性要求:密码必须同时包含字母和数字")
    })
    private String password;
}
