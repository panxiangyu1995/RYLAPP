package com.ryl.miniprogram.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 账号密码登录DTO
 */
@Data
public class LoginDTO {
    
    /**
     * 登录用户名（存储在contact字段）
     */
    @NotBlank(message = "用户名不能为空")
    private String contact;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
} 