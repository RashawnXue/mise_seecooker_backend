package com.seecooker.common.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.seecooker.common.Result;
import com.seecooker.common.exception.BizException;
import com.seecooker.common.exception.ErrorType;
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
    public Result<Void> handleBusinessException(BizException e) {
        log.error(e.getMessage());
        return Result.error(e.getErrorType(), e.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLoginException(NotLoginException e) {
        log.error(e.getMessage());
        return Result.error(ErrorType.USER_NOT_LOGIN, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return Result.error(ErrorType.ILLEGAL_ARGUMENTS, e.getBindingResult().getAllErrors().stream().findFirst().get().getDefaultMessage());
    }
}
