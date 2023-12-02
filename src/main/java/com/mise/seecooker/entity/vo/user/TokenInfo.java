package com.mise.seecooker.entity.vo.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * token信息类
 *
 * @author xueruichen
 * @date 2023.11.27
 */
@Getter
@Setter
@Builder
public class TokenInfo {
    /**
     * token名，satoken
     */
    private String tokenName;

    /**
     * token值
     */
    private String tokenValue;
}
