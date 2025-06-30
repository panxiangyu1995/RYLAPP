package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用户信息请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    /**
     * 手机号码
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 工作地点
     */
    private String location;
} 