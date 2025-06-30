package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 令牌
     */
    private String token;
    
    /**
     * 令牌过期时间
     */
    private String tokenExpire;
    
    /**
     * 工号
     */
    private String workId;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 手机号
     */
    private String mobile;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 地点
     */
    private String location;
    
    /**
     * 头像
     */
    private String avatar;
} 