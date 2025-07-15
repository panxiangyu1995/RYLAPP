package com.ryl.engineer.dto.request;

import lombok.Data;

/**
 * 拒绝任务请求的数据传输对象
 */
@Data
public class RejectTaskRequest {

    /**
     * 拒绝理由
     */
    private String reason;

    /**
     * 转派目标
     * - "admin": 表示转派给管理员
     * - "{userId}": 表示转派给指定ID的工程师
     */
    private String transferTarget;
} 