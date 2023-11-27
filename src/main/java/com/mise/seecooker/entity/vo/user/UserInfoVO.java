package com.mise.seecooker.entity.vo.user;

import com.aliyuncs.exceptions.ClientException;
import com.mise.seecooker.util.AliOSSUtil;
import lombok.Builder;
import lombok.Getter;

/**
 * 用户信息VO类
 *
 * @author xueruichen
 * @date 2023.11.23
 */
@Getter
@Builder
public class UserInfoVO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像url
     */
    private String avatar;
}
