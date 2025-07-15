package com.ryl.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 任务评价实体类
 */
@Data
@TableName("service_evaluation")
public class TaskEvaluation {
    /**
     * 评价ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务ID
     */
    @TableField("task_id")
    private String taskId;
    
    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Long customerId;
    
    /**
     * 服务态度评分（1-5）
     */
    @TableField("service_attitude")
    private Integer serviceAttitude;
    
    /**
     * 服务质量评分（1-5）
     */
    @TableField("service_quality")
    private Integer serviceQuality;
    
    /**
     * 总体评价评分（1-5）
     */
    @TableField("overall_rating")
    private Integer overallRating;
    
    /**
     * 评价内容
     */
    @TableField("comment")
    private String comment;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
}