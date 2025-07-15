package com.ryl.miniprogram.dto;

import lombok.Data;

/**
 * 用户信息更新DTO
 */
@Data
public class UserInfoDTO {
    
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
     * 部门
     */
    private String department;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
} 