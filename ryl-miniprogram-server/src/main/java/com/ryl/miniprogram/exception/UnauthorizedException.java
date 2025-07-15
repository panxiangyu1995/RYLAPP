package com.ryl.miniprogram.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 未授权异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UnauthorizedException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private Integer code = 401;
    
    /**
     * 错误信息
     */
    private String message;
    
    public UnauthorizedException(String message) {
        super(message);
        this.message = message;
    }
} 