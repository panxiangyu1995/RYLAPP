package com.ryl.engineer.dto;

import lombok.Data;

/**
 * 车辆打卡记录统计响应DTO
 */
@Data
public class VehicleRecordStatsResponse {
    
    /**
     * 统计月份，格式：yyyy年MM月
     */
    private String month;
    
    /**
     * 打卡次数
     */
    private Integer checkInCount;
    
    /**
     * 任务数
     */
    private Integer taskCount;
    
    /**
     * 总里程（公里）
     */
    private Double totalDistance;
} 