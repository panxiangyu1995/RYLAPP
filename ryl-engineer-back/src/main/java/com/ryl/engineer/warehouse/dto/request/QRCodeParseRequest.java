package com.ryl.engineer.warehouse.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 二维码解析请求DTO
 */
@Data
public class QRCodeParseRequest {

    /**
     * 二维码内容
     */
    @NotBlank(message = "二维码内容不能为空")
    private String content;
} 