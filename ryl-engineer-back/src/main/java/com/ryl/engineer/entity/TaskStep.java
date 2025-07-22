package com.ryl.engineer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务步骤实体类
 */
@Data
@TableName("task_step")
public class TaskStep {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 步骤索引
     */
    private Integer stepIndex;
    
    /**
     * 步骤标题
     */
    @TableField("title")
    private String title;

    /**
     * 步骤的业务标识符，用于前端识别特殊步骤，如 'site-visit-decision'
     */
    @TableField("step_key")
    private String stepKey;

    /**
     * 步骤状态 (pending, in-progress, completed, skipped)
     */
    private String status;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 预计时间
     */
    private String estimatedTime;
    
    /**
     * 操作人ID
     */
    private Long operatorId;
    
    /**
     * 操作人
     */
    private String operator;
    
    /**
     * 步骤可选项（JSON格式）
     */
    private String options;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 