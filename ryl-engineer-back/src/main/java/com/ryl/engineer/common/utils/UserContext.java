package com.ryl.engineer.common.utils;

import com.ryl.engineer.entity.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 用户上下文工具类，用于获取当前登录用户
 */
public class UserContext {

    private static final String USER_KEY = "current_user";

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户，如果未登录则返回null
     */
    public static User getCurrentUser() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        return (User) session.getAttribute(USER_KEY);
    }

    /**
     * 设置当前登录用户
     *
     * @param user 用户信息
     */
    public static void setCurrentUser(User user) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(true);
        session.setAttribute(USER_KEY, user);
    }

    /**
     * 清除当前登录用户
     */
    public static void clearCurrentUser() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(USER_KEY);
        }
    }
} 