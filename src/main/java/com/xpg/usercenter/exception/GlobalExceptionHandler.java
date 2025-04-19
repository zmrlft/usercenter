package com.xpg.usercenter.exception;

import com.xpg.usercenter.common.BaseResponse;
import com.xpg.usercenter.common.ErrorCode;
import com.xpg.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author xpg
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public static BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException: " + e.getMessage());
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public static BaseResponse RuntimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException: " + e.getMessage());
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }

}
