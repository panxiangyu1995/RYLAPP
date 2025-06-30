package com.ryl.engineer.utils;

/**
 * 用户上下文持有者，用于在当前线程中存储用户信息
 */
public class UserContextHolder {
    
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    
    /**
     * 设置当前用户ID
     * @param userId 用户ID
     */
    public static void setUserId(Long userId) {
        userIdHolder.set(userId);
    }
    
    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    public static Long getUserId() {
        return userIdHolder.get();
    }
    
    /**
     * 清除当前用户ID
     */
    public static void clear() {
        userIdHolder.remove();
    }
} 