package com.ryl.engineer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务工程师关联实体类
 */
@Data
@TableName("task_engineer")
public class TaskEngineer {
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
     * 工程师ID
     */
    private Long engineerId;
    
    /**
     * 是否为任务负责人（0-否，1-是）
     */
    private Integer isOwner;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 