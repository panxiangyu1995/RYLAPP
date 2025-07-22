package com.ryl.engineer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务步骤执行记录实体类
 */
@Data
@TableName("step_record")
public class TaskStepRecord {

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
     * 任务步骤表(task_step)的ID
     */
    private Long stepId;

    /**
     * 步骤索引
     */
    private Integer stepIndex;

    /**
     * 工作内容描述
     */
    @TableField("description")
    private String content;

    /**
     * 花费时间 (分钟)
     */
    private Integer spentTime;

    /**
     * 操作人ID
     */
    @TableField("creator_id")
    private Long operatorId;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 