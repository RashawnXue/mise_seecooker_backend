package com.mise.seecooker.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 图片类型
 *
 * @author xueruichen
 * @date 2023.11.24
 */
@AllArgsConstructor
@Getter
public enum ImageType {
    AVATAR("avatar"),
    POST_IMAGE("post-image"),
    RECIPE_COVER_IMAGE("recipe-cover-image"),
    RECIPE_STEP_IMAGE("recipe-step-image");
    private final String type;
}
