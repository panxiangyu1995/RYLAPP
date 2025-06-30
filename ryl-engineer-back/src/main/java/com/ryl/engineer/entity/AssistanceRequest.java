package com.ryl.engineer.entity;

import lombok.Data;
import lombok.ToString;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 协助请求实体类
 */
@Data
@ToString
@TableName("assistance_request")
public class AssistanceRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 请求ID
     */
    @TableField("request_id")
    private String requestId;
    
    /**
     * 请求标题
     */
    private String title;
    
    /**
     * 请求内容
     */
    @TableField("description")
    private String content;
    
    /**
     * 请求类型
     */
    @TableField("urgency")
    private String type;
    
    /**
     * 关联任务ID
     */
    @TableField("task_id")
    private String taskId;
    
    /**
     * 请求者ID
     */
    @TableField("requester_id")
    private Long requesterId;
    
    /**
     * 请求者名称
     */
    @TableField("requester_name")
    private String requesterName;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 回复内容
     */
    @TableField("response")
    private String responseContent;
    
    /**
     * 回复者ID
     */
    @TableField("engineer_id")
    private Long responderId;
    
    /**
     * 回复者名称
     */
    @TableField("engineer_name")
    private String responderName;
    
    /**
     * 回复时间
     */
    @TableField("handle_time")
    private Date responseTime;
    
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