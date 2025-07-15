package com.ryl.miniprogram.security;

import com.ryl.miniprogram.config.JwtConfig;
import com.ryl.miniprogram.exception.UnauthorizedException;
import com.ryl.miniprogram.util.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT认证拦截器
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
        // 如果不是映射到方法，直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        // 检查是否有@NoAuth注解，有则跳过认证
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (handlerMethod.getMethodAnnotation(NoAuth.class) != null) {
            return true;
        }
        
            // 安全检查配置项
            if (jwtConfig.getHeader() == null || jwtConfig.getTokenHead() == null) {
                throw new UnauthorizedException("系统认证配置错误");
            }
            
            // 从请求头中获取token，增加防御性检查
            String authHeader = null;
            try {
                authHeader = request.getHeader(jwtConfig.getHeader());
            } catch (Exception e) {
                throw new UnauthorizedException("无法获取认证信息");
            }
            
            // 检查认证头是否存在且格式正确
            if (StringUtils.isBlank(authHeader)) {
            throw new UnauthorizedException("请先登录");
        }
        
            if (!authHeader.startsWith(jwtConfig.getTokenHead())) {
                throw new UnauthorizedException("认证格式不正确");
            }
            
            // 安全截取token
            String token = "";
            try {
                token = authHeader.substring(jwtConfig.getTokenHead().length()).trim();
            } catch (Exception e) {
                throw new UnauthorizedException("令牌格式错误");
            }
            
            // 验证token是否为空
            if (StringUtils.isBlank(token)) {
                throw new UnauthorizedException("令牌不能为空");
            }
        
        // 验证token是否有效
        if (!jwtTokenUtil.validateToken(token)) {
            throw new UnauthorizedException("登录已过期，请重新登录");
        }
        
        // 获取用户信息并存入请求属性
            try {
        String openid = jwtTokenUtil.getOpenIdFromToken(token);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
                
                if (userId == null) {
                    throw new UnauthorizedException("无法识别用户信息");
                }
                
        request.setAttribute("openid", openid);
        request.setAttribute("userId", userId);
            } catch (Exception e) {
                throw new UnauthorizedException("令牌解析失败");
            }
        
        return true;
        } catch (UnauthorizedException e) {
            throw e; // 直接重新抛出业务异常
        } catch (Exception e) {
            // 捕获所有其他异常，避免未处理异常导致系统崩溃
            throw new UnauthorizedException("认证过程发生错误");
        }
    }
} 