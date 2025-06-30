package com.ryl.engineer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 注册请求DTO
 */
@Data
public class RegisterRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 工号
     */
    @NotBlank(message = "工号不能为空")
    private String workId;
    
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 地点
     */
    private String location;
} 