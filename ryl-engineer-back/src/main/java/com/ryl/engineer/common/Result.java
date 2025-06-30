package com.ryl.engineer.common;

import java.io.Serializable;

/**
 * 通用响应结果封装类
 * @param <T> 数据类型
 */
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码
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

    public Result() {
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 成功响应
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功");
    }
    
    /**
     * 成功响应
     * @param data 数据
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    /**
     * 成功响应
     * @param message 消息
     * @param data 数据
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    /**
     * 失败响应
     * @param code 状态码
     * @param message 消息
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }
    
    /**
     * 失败响应
     * @param message 消息
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }
    
    /**
     * 参数错误响应
     * @param message 消息
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message);
    }

    /**
     * 未授权响应
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权，请先登录");
    }

    /**
     * 权限不足响应
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "权限不足");
    }
    
    /**
     * 资源不存在响应
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> notFound() {
        return new Result<>(404, "资源不存在");
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
} 