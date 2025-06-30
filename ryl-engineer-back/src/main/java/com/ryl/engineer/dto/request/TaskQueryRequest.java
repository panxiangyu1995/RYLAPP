package com.ryl.engineer.dto.request;

import lombok.Data;

/**
 * 任务查询请求DTO
 */
@Data
public class TaskQueryRequest {
    /**
     * 页码，默认1
     */
    private Integer page = 1;
    
    /**
     * 每页记录数，默认10
     */
    private Integer size = 10;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 任务类型
     */
    private String taskType;
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 开始日期
     */
    private String startDate;
    
    /**
     * 结束日期
     */
    private String endDate;
    
    /**
     * 优先级
     */
    private String priority;
    
    /**
     * 工程师ID
     */
    private Long engineerId;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 销售ID
     */
    private Long salesId;
} 