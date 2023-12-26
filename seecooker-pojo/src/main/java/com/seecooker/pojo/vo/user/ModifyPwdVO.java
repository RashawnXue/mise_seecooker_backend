package com.seecooker.pojo.vo.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改密码VO类
 *
 * @author xueruichen
 * @date 2023.12.27
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ModifyPwdVO {
    /**
     * 用户名
     */
    @NotNull
    private String username;

    /**
     * 原密码
     */
    @NotNull
    private String password;

    /**
     * 新密码
     */
    @NotNull
    @Size(min = 6, max = 56, message = "密码长度必须在 6-56 之间")
    @Pattern.List({
            @Pattern(regexp = "^[\\x21-\\x7e]*$", message = "密码只能包含字母,数字和符号"),
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).*$", message = "密码未达到复杂性要求:密码必须同时包含字母和数字")
    })
    private String newPassword;
}
