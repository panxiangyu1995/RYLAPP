package com.ryl.engineer.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {

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
     * 成功响应
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return ResponseDTO
     */
    public static <T> ResponseDTO<T> success(T data) {
        ResponseDTO<T> responseDTO = new ResponseDTO<>();
        responseDTO.setCode(200);
        responseDTO.setMessage("操作成功");
        responseDTO.setData(data);
        return responseDTO;
    }

    /**
     * 成功响应
     *
     * @param message 消息
     * @param data    数据
     * @param <T>     数据类型
     * @return ResponseDTO
     */
    public static <T> ResponseDTO<T> success(String message, T data) {
        ResponseDTO<T> responseDTO = new ResponseDTO<>();
        responseDTO.setCode(200);
        responseDTO.setMessage(message);
        responseDTO.setData(data);
        return responseDTO;
    }

    /**
     * 失败响应
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     数据类型
     * @return ResponseDTO
     */
    public static <T> ResponseDTO<T> error(Integer code, String message) {
        ResponseDTO<T> responseDTO = new ResponseDTO<>();
        responseDTO.setCode(code);
        responseDTO.setMessage(message);
        return responseDTO;
    }

    /**
     * 参数错误响应
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return ResponseDTO
     */
    public static <T> ResponseDTO<T> paramError(String message) {
        return error(400, message);
    }

    /**
     * 未授权响应
     *
     * @param <T> 数据类型
     * @return ResponseDTO
     */
    public static <T> ResponseDTO<T> unauthorized() {
        return error(401, "未授权");
    }

    /**
     * 权限不足响应
     *
     * @param <T> 数据类型
     * @return ResponseDTO
     */
    public static <T> ResponseDTO<T> forbidden() {
        return error(403, "权限不足");
    }

    /**
     * 资源不存在响应
     *
     * @param <T> 数据类型
     * @return ResponseDTO
     */
    public static <T> ResponseDTO<T> notFound() {
        return error(404, "资源不存在");
    }

    /**
     * 服务器内部错误响应
     *
     * @param <T> 数据类型
     * @return ResponseDTO
     */
    public static <T> ResponseDTO<T> serverError() {
        return error(500, "服务器内部错误");
    }
} 