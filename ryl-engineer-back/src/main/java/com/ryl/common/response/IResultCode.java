package com.ryl.common.response;

/**
 * API返回码接口
 */
public interface IResultCode {

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    Integer getCode();

    /**
     * 获取消息
     *
     * @return 消息
     */
    String getMessage();
} 