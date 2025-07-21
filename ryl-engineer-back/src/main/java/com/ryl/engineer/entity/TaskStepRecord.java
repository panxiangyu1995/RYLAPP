package com.ryl.engineer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务步骤执行记录实体类
 */
@Data
@TableName("task_step_record")
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
     * 工作内容描述
     */
    private String content;

    /**
     * 花费时间 (分钟)
     */
    private Integer spentTime;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 