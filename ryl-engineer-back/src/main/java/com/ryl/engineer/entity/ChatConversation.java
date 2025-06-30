package com.ryl.engineer.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天会话实体类
 * 对应表：chat_conversation
 */
@Data
public class ChatConversation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 会话ID
     */
    private String conversationId;
    
    /**
     * 会话类型（single-单聊, group-群聊）
     */
    private String conversationType;
    
    /**
     * 会话名称（群聊时使用）
     */
    private String name;
    
    /**
     * 会话头像（群聊时使用）
     */
    private String avatar;
    
    /**
     * 创建者ID
     */
    private Long creatorId;
    
    /**
     * 最后一条消息内容
     */
    private String lastMessageContent;
    
    /**
     * 最后一条消息时间
     */
    private Date lastMessageTime;
    
    /**
     * 最后一条消息发送者ID
     */
    private Long lastMessageSenderId;
    
    /**
     * 成员数量
     */
    private Integer memberCount;
    
    /**
     * 是否与任务相关（0-否，1-是）
     */
    private Integer isTaskRelated;
    
    /**
     * 关联任务ID
     */
    private String relatedTaskId;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 状态（active-活跃, deleted-已删除）
     */
    private String status;
} 