package com.ryl.engineer.common.response;

import lombok.Data;

/**
 * 统一API响应结果封装
 */
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功返回结果
     *
     * @param message 提示信息
     * @param data 返回数据
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 失败返回结果
     *
     * @param code 错误码
     * @param message 错误信息
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> failure(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败返回结果
     *
     * @param code 错误码
     * @param message 错误信息
     * @param data 返回数据
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> failure(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> validateFailed(String message) {
        return new Result<>(400, message, null);
    }

    /**
     * 未登录返回结果
     *
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "暂未登录或token已经过期", null);
    }

    /**
     * 未授权返回结果
     *
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "没有相关权限", null);
    }
} 