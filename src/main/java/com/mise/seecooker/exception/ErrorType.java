package com.mise.seecooker.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误类型枚举类
 *
 * @author xueruichen
 * @date 2023.11.18
 */
@Getter
@AllArgsConstructor
public enum ErrorType {
    /**
     * 常见错误类型
     */
    UNKNOWN_ERROR(100000, "Unknown Error", 500),/* 未知错误 */

    BAD_REQUEST(100001, "Bad Request", 400),/* 客户端请求的语法错误 */

    ILLEGAL_ARGUMENTS(100002, "Illegal Arguments", 400),/* 非法参数 */

    UNAUTHORIZED(100003, "Unauthorized", 401),/* 服务端请求要求用户的身份认证 */

    FORBIDDEN(100004, "Forbidden", 403),/* 服务端拒绝执行请求 */

    NOT_FOUND(100005, "Not Found", 404),/* 服务端无法找到客户端请求的资源 */

    /**
     * 业务相关错误类型
     */
    USER_NOT_LOGIN(200001, "User not login", 400), /* 用户未登陆 */

    PASSWORD_ERROR(200002, "Password error", 400), /* 用户密码输入错误 */

    USER_NOT_EXIST(200003, "User not exist", 400), /* 用户不存在 */

    USER_ALREADY_EXIST(200004, "User already exist", 400), /* 用户不存在 */

    FILE_NOT_NAMED(300001, "File not named", 400), /* 文件未命名 */

    FILE_SIGNED_ERROR(300002, "File signed error", 400), /* 文件签名错误 */

    POST_NOT_EXIST(400001, "Post not exist", 400), /* 帖子不存在 */

    RECIPE_STEP_MATCH_ERROR(500001, "Recipe step match error", 400); /*菜谱步骤图和步骤文字不匹配*/
    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    /**
     * http状态码
     */
    private final int httpCode;
}
