package com.ryl.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 构造方法
     */
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造方法
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     */
    private Result(Integer code, String message, T data, Boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功返回结果
     *
     * @param <T> 泛型参数
     * @return 成功结果
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null, true);
    }

    /**
     * 成功返回结果
     *
     * @param data 数据
     * @param <T>  泛型参数
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, true);
    }

    /**
     * 成功返回结果
     *
     * @param data    数据
     * @param message 消息
     * @param <T>     泛型参数
     * @return 成功结果
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data, true);
    }

    /**
     * 失败返回结果
     *
     * @param <T> 泛型参数
     * @return 失败结果
     */
    public static <T> Result<T> failed() {
        return new Result<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage(), null, false);
    }

    /**
     * 失败返回结果
     *
     * @param message 消息
     * @param <T>     泛型参数
     * @return 失败结果
     */
    public static <T> Result<T> failed(String message) {
        return new Result<>(ResultCode.FAILED.getCode(), message, null, false);
    }

    /**
     * 失败返回结果
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     泛型参数
     * @return 失败结果
     */
    public static <T> Result<T> failed(Integer code, String message) {
        return new Result<>(code, message, null, false);
    }

    /**
     * 失败返回结果
     *
     * @param resultCode 结果枚举
     * @param <T>        泛型参数
     * @return 失败结果
     */
    public static <T> Result<T> failed(IResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null, false);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param <T> 泛型参数
     * @return 参数验证失败结果
     */
    public static <T> Result<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 消息
     * @param <T>     泛型参数
     * @return 参数验证失败结果
     */
    public static <T> Result<T> validateFailed(String message) {
        return new Result<>(ResultCode.VALIDATE_FAILED.getCode(), message, null, false);
    }

    /**
     * 未登录返回结果
     *
     * @param <T> 泛型参数
     * @return 未登录结果
     */
    public static <T> Result<T> unauthorized() {
        return failed(ResultCode.UNAUTHORIZED);
    }

    /**
     * 未授权返回结果
     *
     * @param <T> 泛型参数
     * @return 未授权结果
     */
    public static <T> Result<T> forbidden() {
        return failed(ResultCode.FORBIDDEN);
    }
} 