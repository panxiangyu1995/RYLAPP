package com.ryl.engineer.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {

    private static String secret;

    private static long expiration;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        JwtUtil.secret = secret;
    }

    @Value("${jwt.expiration}")
    public void setExpiration(long expiration) {
        JwtUtil.expiration = expiration;
    }

    /**
     * 生成token
     *
     * @param userId 用户ID
     * @param workId 工号
     * @return token
     */
    public static String generateToken(Long userId, String workId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("workId", workId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从token中获取Claims
     *
     * @param token token
     * @return Claims
     */
    public static Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从token中获取用户ID
     *
     * @param token token
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从token中获取用户ID
     *
     * @param token token
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        try {
            if (token == null || !validateToken(token)) {
                return null;
            }
            return getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从token中获取工号
     *
     * @param token token
     * @return 工号
     */
    public static String getWorkIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("workId", String.class);
    }

    /**
     * 验证token是否过期
     *
     * @param token token
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 验证token是否有效
     *
     * @param token token
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
} 