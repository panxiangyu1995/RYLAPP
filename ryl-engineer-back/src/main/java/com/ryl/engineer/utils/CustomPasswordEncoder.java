package com.ryl.engineer.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 自定义密码编码器工具类
 */
@Component
public class CustomPasswordEncoder {
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * 对密码进行编码
     * @param password 原始密码
     * @return 编码后的密码
     */
    public String encode(String password) {
        return encoder.encode(password);
    }
    
    /**
     * 验证密码是否匹配
     * @param rawPassword 原始密码
     * @param encodedPassword 编码后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
} 