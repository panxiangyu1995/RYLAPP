package com.ryl.engineer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 任务活动记录实体类
 */
@Data
@TableName("task_activity")
public class TaskActivity {
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
     * 活动类型
     */
    private String activityType;
    
    /**
     * 活动内容
     */
    private String content;
    
    /**
     * 操作人ID
     */
    private Long operatorId;
    
    /**
     * 操作人姓名
     */
    private String operatorName;
    
    /**
     * 创建时间
     */
    private Date createTime;
} 