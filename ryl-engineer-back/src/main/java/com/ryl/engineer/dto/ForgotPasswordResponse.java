package com.ryl.engineer.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 忘记密码响应DTO
 */
@Data
public class ForgotPasswordResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 是否重置成功
     */
    private Boolean reset;
    
    /**
     * 工号
     */
    private String workId;
} 