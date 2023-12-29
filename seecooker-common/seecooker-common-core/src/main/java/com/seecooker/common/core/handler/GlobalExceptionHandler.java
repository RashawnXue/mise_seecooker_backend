package com.seecooker.common.core.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.seecooker.common.core.model.Result;
import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;
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
    /**
     * 处理不可控异常
     *
     * @param e 异常
     * @return 响应结果
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handException(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return Result.error(ErrorType.SERVER_ERROR, e.getMessage());
    }

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 响应结果
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBusinessException(BizException e) {
        log.error(e.getMessage());
        return Result.error(e.getErrorType(), e.getMessage());
    }

    /**
     * 处理token异常
     *
     * @param e token异常
     * @return 响应结果
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLoginException(NotLoginException e) {
        log.error(e.getMessage());
        return Result.error(ErrorType.USER_NOT_LOGIN, "用户未登陆");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return Result.error(ErrorType.ILLEGAL_ARGUMENTS, e.getBindingResult().getAllErrors().stream().findFirst().get().getDefaultMessage());
    }
}
