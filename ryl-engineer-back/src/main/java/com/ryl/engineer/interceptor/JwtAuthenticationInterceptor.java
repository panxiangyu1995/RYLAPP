package com.ryl.engineer.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryl.engineer.common.Result;
import com.ryl.engineer.util.JwtUtil;
import com.ryl.engineer.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT认证拦截器
 * 从请求头中提取JWT token，验证并设置用户上下文
 */
@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationInterceptor.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;
        
        // 提取token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        
        // 记录当前请求路径和token状态
        logger.debug("请求URL: {}, token: {}", request.getRequestURI(), token != null ? "已提供" : "未提供");
        
        if (token != null && !token.isEmpty()) {
            try {
                // 验证token并获取用户ID
                Long userId = JwtUtil.getUserId(token);
                if (userId != null) {
                    // 设置用户上下文
                    UserContextHolder.setUserId(userId);
                    logger.info("JWT认证成功，用户ID: {}", userId);
                    return true; // 认证成功，允许请求继续
                } else {
                    logger.warn("JWT解析失败，无法获取用户ID");
                    handleAuthError(response, "JWT令牌无效", 401);
                    return false;
                }
            } catch (Exception e) {
                logger.error("JWT解析异常: {}", e.getMessage());
                handleAuthError(response, "JWT令牌解析失败: " + e.getMessage(), 401);
                return false;
            }
        } else {
            logger.debug("请求未提供有效token");
            
            // 检查是否为需要认证的API路径
            String requestURI = request.getRequestURI();
            // 判断是否为排除的公开API路径（如登录、注册等）
            if (isPublicApi(requestURI)) {
                logger.debug("访问公开API，不需要认证: {}", requestURI);
                return true;
            }
            
            handleAuthError(response, "请提供认证令牌", 401);
            return false;
        }
    }

    /**
     * 判断是否为公开API（不需要认证的API）
     */
    private boolean isPublicApi(String uri) {
        return uri.contains("/api/v1/user/login") || 
               uri.contains("/api/v1/user/register") || 
               uri.contains("/api/v1/user/forgot-password") || 
               uri.contains("/api/v1/user/reset-password") ||
               uri.contains("/api/v1/uploads");
    }

    /**
     * 处理认证错误
     */
    private void handleAuthError(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        Result<Object> result = Result.error(message);
        writer.write(objectMapper.writeValueAsString(result));
        writer.flush();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal，防止内存泄漏
        UserContextHolder.clear();
        logger.debug("请求完成，已清理用户上下文");
    }
} 