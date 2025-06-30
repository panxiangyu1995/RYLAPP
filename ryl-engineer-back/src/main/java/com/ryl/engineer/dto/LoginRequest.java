package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 工号
     */
    @NotBlank(message = "工号不能为空")
    private String workId;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
} 