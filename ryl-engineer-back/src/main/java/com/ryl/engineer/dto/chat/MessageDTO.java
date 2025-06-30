package com.ryl.engineer.dto.chat;

import lombok.Data;

import java.util.Date;

/**
 * 聊天消息数据传输对象
 */
@Data
public class MessageDTO {
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 会话ID
     */
    private String conversationId;
    
    /**
     * 发送者信息
     */
    private SenderInfo sender;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型(chat/task/system)
     */
    private String messageType;
    
    /**
     * 内容类型(text/image/file)
     */
    private String contentType;
    
    /**
     * 发送时间
     */
    private Date sendTime;
    
    /**
     * 消息状态
     */
    private String status;
    
    /**
     * 当前用户是否已读
     */
    private Boolean isRead;
    
    /**
     * 引用回复的消息信息
     */
    private ReplyInfo replyTo;
    
    /**
     * 发送者信息内部类
     */
    @Data
    public static class SenderInfo {
        /**
         * 发送者ID
         */
        private Long id;
        
        /**
         * 发送者姓名
         */
        private String name;
        
        /**
         * 发送者头像
         */
        private String avatar;
    }
    
    /**
     * 引用回复信息内部类
     */
    @Data
    public static class ReplyInfo {
        /**
         * 引用消息ID
         */
        private String messageId;
        
        /**
         * 引用消息内容
         */
        private String content;
        
        /**
         * 引用消息发送者ID
         */
        private Long senderId;
        
        /**
         * 引用消息发送者姓名
         */
        private String senderName;
    }
} 