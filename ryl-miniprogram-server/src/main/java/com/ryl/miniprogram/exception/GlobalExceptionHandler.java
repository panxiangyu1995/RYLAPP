package com.ryl.miniprogram.exception;

import com.ryl.miniprogram.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理自定义的业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResultVO<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return ResultVO.failed(e.getMessage(), e.getCode());
    }
    
    /**
     * 处理未授权异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResultVO<Void> handleUnauthorizedException(UnauthorizedException e) {
        log.error("未授权异常：{}", e.getMessage());
        return ResultVO.unauthorized(e.getMessage());
    }
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getDefaultMessage();
            }
        }
        return ResultVO.validateFailed(message == null ? "参数校验失败" : message);
    }
    
    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResultVO<Void> handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getDefaultMessage();
            }
        }
        return ResultVO.validateFailed(message == null ? "参数绑定失败" : message);
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResultVO<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return ResultVO.failed("系统异常，请稍后重试");
    }
} 