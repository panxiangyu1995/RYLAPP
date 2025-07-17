package com.ryl.miniprogram.dto;

import lombok.Data;

/**
 * 测试发送订阅消息请求的数据传输对象
 */
@Data
public class TestNotificationRequest {

    /**
     * 模板类型
     * 可选值: engineer_assigned, quote_generated, price_confirmed, service_completed, evaluation_received
     */
    private String notificationType;
} 