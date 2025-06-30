package com.ryl.engineer.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 协助请求实体类
 */
@Data
@ToString
public class AssistanceRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 请求标题
     */
    private String title;
    
    /**
     * 请求内容
     */
    private String content;
    
    /**
     * 请求类型
     */
    private String type;
    
    /**
     * 关联任务ID
     */
    private String taskId;
    
    /**
     * 请求者ID
     */
    private Long requesterId;
    
    /**
     * 请求者名称
     */
    private String requesterName;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 回复内容
     */
    private String responseContent;
    
    /**
     * 回复者ID
     */
    private Long responderId;
    
    /**
     * 回复者名称
     */
    private String responderName;
    
    /**
     * 回复时间
     */
    private Date responseTime;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 