package com.ryl.engineer.util;

import org.springframework.stereotype.Component;

/**
 * 密码工具类
 */
@Component
public class PasswordUtil {
    
    /**
     * 密码加密（现在不做加密，直接返回明文）
     *
     * @param password 明文密码
     * @return 明文密码
     */
    public static String encode(String password) {
        return password;
    }
    
    /**
     * 密码验证（现在直接比较明文）
     *
     * @param rawPassword     明文密码
     * @param storedPassword  存储的密码
     * @return 是否匹配
     */
    public static boolean verify(String rawPassword, String storedPassword) {
        return rawPassword.equals(storedPassword);
    }
} 