package com.ryl.miniprogram.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户注册DTO
 */
@Data
public class RegisterDTO {
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 登录用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String contact;
    
    /**
     * 公司名
     */
    @NotBlank(message = "公司名不能为空")
    private String name;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 联系地址
     */
    private String address;
    
    /**
     * 联系邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
} 