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
    @NotBlank(message = "登录凭证code不能为空")
    private String code;
    
    // 新增字段，用于接收前端传递的用户昵称
    private String nickname;
    
    // 新增字段，用于接收前端传递的用户头像
    private String avatarUrl;

    // 新增字段，用于接收前端传递的手机号加密凭证
    private String phoneCode;
} 