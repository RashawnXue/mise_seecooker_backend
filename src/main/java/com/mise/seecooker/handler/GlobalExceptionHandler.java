package com.mise.seecooker.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.mise.seecooker.entity.Result;
import com.mise.seecooker.exception.BizException;
import com.mise.seecooker.exception.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 *
 * @author xueruichen
 * @date 2023.11.23
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BizException.class)
    public Result<?> handleBusinessException(BizException e) {
        log.error(e.getMessage());
        return Result.error(e.getErrorType(), e.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public Result<?> handleNotLoginException(NotLoginException e) {
        log.error(e.getMessage());
        return Result.error(ErrorType.USER_NOT_LOGIN, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return Result.error(ErrorType.ILLEGAL_ARGUMENTS, e.getBindingResult().getAllErrors().stream().findFirst().get().getDefaultMessage());
    }
}
