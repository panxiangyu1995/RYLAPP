package com.ryl.engineer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 忘记密码请求DTO
 */
@Data
public class ForgotPasswordRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 工号
     */
    @NotBlank(message = "工号不能为空")
    private String workId;
    
    /**
     * 手机号码
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号码")
    private String mobile;
    
    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
    
    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
} 