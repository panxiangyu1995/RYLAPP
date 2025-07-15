package com.ryl.miniprogram.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 微信登录DTO
 */
@Data
public class WxLoginDTO {
    
    /**
     * 微信登录code
     */
    @NotBlank(message = "code不能为空")
    private String code;
} 