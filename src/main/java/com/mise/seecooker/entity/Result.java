package com.mise.seecooker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mise.seecooker.exception.ErrorType;
import lombok.Getter;

/**
 * 返回结果封装类Result
 *
 * @param <T> 返回值data
 * @author xueruichen
 * @date 2023.11.17
 */
@Getter
public class Result<T> {
    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态信息
     */
    private final String message;

    /**
     * 返回数据
     */
    private final T data;

    /**
     * http状态码
     */
    @JsonIgnore
    private final Integer httpCode;

    public Result(Integer code, String message, T data, Integer httpCode) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.httpCode = httpCode;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data, 200);
    }

    public static <T> Result<T> success() {
        return new Result<>(0, "success", null, 200);
    }

    public static <T> Result<T> error(ErrorType type) {
        return new Result<>(type.getCode(), type.getMessage(), null, type.getHttpCode());
    }

    public static <T> Result<T> error(ErrorType type, String message) {
        return new Result<>(type.getCode(), message, null, type.getHttpCode());
    }
}
