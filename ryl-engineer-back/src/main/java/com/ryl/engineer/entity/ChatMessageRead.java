package com.ryl.engineer.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息已读状态实体类
 * 对应表：chat_message_read
 */
@Data
public class ChatMessageRead implements Serializable {
    
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
     * 用户ID
     */
    private Long userId;
    
    /**
     * 阅读时间
     */
    private Date readTime;
} 