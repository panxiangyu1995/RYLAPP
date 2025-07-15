package com.ryl.miniprogram.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 任务DTO
 */
@Data
public class TaskDTO {
    
    /**
     * 任务ID，更新时必填
     */
    private Long id;
    
    /**
     * 任务编号
     */
    private String taskId;
    
    /**
     * 任务标题
     */
    @NotBlank(message = "任务标题不能为空")
    private String title;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 任务类型（repair：维修，maintenance：保养，recycle：回收，leasing：租赁，
     * training：培训，verification：检测，selection：选型，installation：安装）
     */
    private String taskType;
    
    // 移除description字段，避免与description字段冲突
    
    /**
     * 任务状态（draft/pending/in-progress/completed/cancelled）
     */
    private String status;
    
    /**
     * 优先级（low/normal/high）
     */
    private String priority;
    
    /**
     * 开始时间
     */
    private Date startDate;
    
    /**
     * 截止时间
     */
    private Date deadline;
    
    /**
     * 设备信息
     */
    private DeviceDTO device;
    
    /**
     * 客户信息
     */
    private CustomerDTO customer;
} 