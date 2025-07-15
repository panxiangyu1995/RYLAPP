package com.ryl.miniprogram.util;

import com.ryl.miniprogram.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JwtTokenUtil {
    
    @Autowired
    private JwtConfig jwtConfig;
    
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String CLAIM_KEY_USER_ID = "userId";
    
    /**
     * 根据用户信息生成JWT token
     */
    public String generateToken(String openid, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, openid);
        claims.put(CLAIM_KEY_CREATED, new Date());
        claims.put(CLAIM_KEY_USER_ID, userId);
        return generateToken(claims);
    }
    
    /**
     * 从token中获取JWT中的负载
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 解析JWT失败
        }
        return claims;
    }
    
    /**
     * 从token中获取用户名
     */
    public String getOpenIdFromToken(String token) {
        String openid = null;
        try {
            Claims claims = getClaimsFromToken(token);
            openid = claims.getSubject();
        } catch (Exception e) {
            // 获取用户名失败
        }
        return openid;
    }
    
    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Long userId = null;
        try {
            Claims claims = getClaimsFromToken(token);
            userId = Long.parseLong(claims.get(CLAIM_KEY_USER_ID).toString());
        } catch (Exception e) {
            // 获取用户ID失败
        }
        return userId;
    }
    
    /**
     * 验证token是否有效
     */
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
    
    /**
     * 判断token是否已经失效
     */
    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.before(new Date());
    }
    
    /**
     * 从token中获取过期时间
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }
    
    /**
     * 根据负载生成JWT token
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }
    
    /**
     * 生成token的过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + jwtConfig.getExpiration() * 1000);
    }
} 