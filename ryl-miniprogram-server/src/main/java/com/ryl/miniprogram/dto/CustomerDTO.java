package com.ryl.miniprogram.dto;

import lombok.Data;

/**
 * 客户信息数据传输对象
 */
@Data
public class CustomerDTO {
    /**
     * 客户ID
     */
    private Long id;
    
    /**
     * 客户名称
     */
    private String name;
    
    /**
     * 联系人
     */
    private String contact;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 电子邮箱
     */
    private String email;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 微信openid
     */
    private String openid;
    
    /**
     * 微信昵称
     */
    private String nickname;
    
    /**
     * 微信头像
     */
    private String avatarUrl;
}