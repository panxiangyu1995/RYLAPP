package com.ryl.engineer.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 注册响应DTO
 */
@Data
public class RegisterResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 注册是否成功
     */
    private Boolean registered;
    
    /**
     * 工号
     */
    private String workId;
    
    /**
     * 姓名
     */
    private String name;
} 