package com.ryl.miniprogram.vo;

import lombok.Data;

/**
 * 统一API响应结果封装
 */
@Data
public class ResultVO<T> {
    /**
     * 状态码，200表示成功，非200表示失败
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 成功返回结果
     */
    public static <T> ResultVO<T> success(T data) {
        return success(data, "操作成功");
    }
    
    /**
     * 成功返回结果
     */
    public static <T> ResultVO<T> success(T data, String message) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
    
    /**
     * 失败返回结果
     */
    public static <T> ResultVO<T> failed(String message) {
        return failed(message, 500);
    }
    
    /**
     * 失败返回结果
     */
    public static <T> ResultVO<T> failed(String message, Integer code) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 参数验证失败返回结果
     */
    public static <T> ResultVO<T> validateFailed(String message) {
        return failed(message, 400);
    }
    
    /**
     * 未登录返回结果
     */
    public static <T> ResultVO<T> unauthorized(String message) {
        return failed(message, 401);
    }
    
    /**
     * 未授权返回结果
     */
    public static <T> ResultVO<T> forbidden(String message) {
        return failed(message, 403);
    }
} 