package com.ryl.engineer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务状态历史记录实体类
 */
@Data
@TableName("task_status_history")
public class TaskStatusHistory {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务编号
     */
    private String taskId;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 备注
     */
    private String comment;
    
    /**
     * 更新人ID
     */
    private Long updatedBy;
    
    /**
     * 更新人姓名
     */
    private String updatedByName;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
} 