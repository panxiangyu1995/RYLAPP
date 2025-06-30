package com.ryl.engineer.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天消息实体类
 * 对应表：chat_message
 */
@Data
public class ChatMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 会话ID
     */
    private String conversationId;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型（chat-聊天消息, task-任务消息, system-系统消息）
     */
    private String messageType;
    
    /**
     * 内容类型（text-文本, image-图片, file-文件）
     */
    private String contentType;
    
    /**
     * 发送时间
     */
    private Date sendTime;
    
    /**
     * 状态（sent-已发送, delivered-已送达, read-已读, failed-发送失败）
     */
    private String status;
    
    /**
     * 回复的消息ID
     */
    private String replyToMessageId;
    
    /**
     * 是否已删除（0-否，1-是）
     */
    private Integer isDeleted;
    
    /**
     * 创建时间
     */
    private Date createTime;
} 