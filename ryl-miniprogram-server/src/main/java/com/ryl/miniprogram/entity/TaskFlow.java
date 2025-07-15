package com.ryl.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 任务流程实体类
 */
@Data
@TableName("task_flow")
public class TaskFlow {
    /**
     * 流程ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务ID
     */
    @TableField("task_id")
    private String taskId;
    
    /**
     * 任务类型
     */
    @TableField("task_type")
    private String taskType;
    
    /**
     * 当前步骤
     */
    @TableField("current_step")
    private Integer currentStep;
    
    /**
     * 状态（0：进行中，1：已完成）
     */
    @TableField(exist = false) 
    private Integer status;
    
    /**
     * 总步骤数
     */
    @TableField(exist = false)
    private Integer totalSteps;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
}