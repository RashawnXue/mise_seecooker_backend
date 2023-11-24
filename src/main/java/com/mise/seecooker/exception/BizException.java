package com.mise.seecooker.exception;

import lombok.Getter;

/**
 * 业务相关异常
 *
 * @author xueruichen
 * @date 2023.11.18
 */
@Getter
public class BizException extends RuntimeException{
    /**
     * 状态码
     */
    private final int code;
    /**
     * 异常信息
     */
    private final String message;
    /**
     * http状态码
     */
    private final int httpCode;
    /**
     * 错误类型
     */
    private final ErrorType errorType;

    public BizException(ErrorType type) {
        this.errorType = type;
        this.code = type.getCode();
        this.message = type.getMessage();
        this.httpCode = type.getHttpCode();
    }

    public BizException(ErrorType type, String message) {
        this.errorType = type;
        this.code = type.getCode();
        this.message = message;
        this.httpCode = type.getHttpCode();
    }
}
