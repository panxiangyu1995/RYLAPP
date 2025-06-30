package com.ryl.engineer.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 密码编码器工具类（兼容旧代码）
 * @deprecated 使用 {@link CustomPasswordEncoder} 代替
 */
@Component("passwordEncoderLegacy")
@Deprecated
public class PasswordEncoder {
    
    @Autowired
    private CustomPasswordEncoder customPasswordEncoder;
    
    /**
     * 对密码进行编码
     * @param password 原始密码
     * @return 编码后的密码
     */
    public String encode(String password) {
        return customPasswordEncoder.encode(password);
    }
    
    /**
     * 验证密码是否匹配
     * @param rawPassword 原始密码
     * @param encodedPassword 编码后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return customPasswordEncoder.matches(rawPassword, encodedPassword);
    }
} 