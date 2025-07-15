package com.ryl.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 任务步骤记录实体类
 */
@Data
@TableName("step_record")
public class TaskStep {
    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务ID
     */
    @TableField("task_id")
    private String taskId;
    
    /**
     * 步骤ID
     */
    @TableField("step_id")
    private Long stepId;
    
    /**
     * 步骤索引
     */
    @TableField("step_index")
    private Integer stepIndex;
    
    /**
     * 步骤描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 创建人ID
     */
    @TableField("creator_id")
    private Long creatorId;
    
    /**
     * 创建人名称
     */
    @TableField("creator_name")
    private String creatorName;
    
    /**
     * 备注
     */
    @TableField(exist = false)
    private String remark;
    
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