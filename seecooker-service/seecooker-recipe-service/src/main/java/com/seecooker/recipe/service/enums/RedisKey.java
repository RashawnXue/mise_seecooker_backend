package com.seecooker.recipe.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Redis key 枚举类
 *
 * @author xueruichen
 * @date 2023.12.10
 */
@AllArgsConstructor
@Getter
public enum RedisKey {
    INGREDIENT("INGREDIENT");

    private final String key;
}
