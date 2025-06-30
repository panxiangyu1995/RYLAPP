package com.ryl.engineer.dto.chat;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 聊天会话数据传输对象
 */
@Data
public class ConversationDTO {
    
    /**
     * 会话ID
     */
    private String conversationId;
    
    /**
     * 会话类型(single/group)
     */
    private String type;
    
    /**
     * 会话名称
     */
    private String name;
    
    /**
     * 会话头像
     */
    private String avatar;
    
    /**
     * 是否与任务相关
     */
    private Boolean isTaskRelated;
    
    /**
     * 关联任务ID
     */
    private String relatedTaskId;
    
    /**
     * 关联任务标题
     */
    private String relatedTaskTitle;
    
    /**
     * 最后一条消息
     */
    private LastMessage lastMessage;
    
    /**
     * 未读消息数量
     */
    private Integer unreadCount;
    
    /**
     * 成员数量
     */
    private Integer memberCount;
    
    /**
     * 是否置顶
     */
    private Boolean isSticky;
    
    /**
     * 是否静音
     */
    private Boolean isMute;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 会话成员列表
     */
    private List<MemberInfo> members;
    
    /**
     * 最后一条消息内部类
     */
    @Data
    public static class LastMessage {
        /**
         * 消息内容
         */
        private String content;
        
        /**
         * 消息类型(chat/task/system)
         */
        private String type;
        
        /**
         * 发送时间
         */
        private Date time;
        
        /**
         * 发送者ID
         */
        private Long senderId;
        
        /**
         * 发送者姓名
         */
        private String senderName;
    }
    
    /**
     * 会话成员信息内部类
     */
    @Data
    public static class MemberInfo {
        /**
         * 用户ID
         */
        private Long id;
        
        /**
         * 用户姓名
         */
        private String name;
        
        /**
         * 用户头像
         */
        private String avatar;
        
        /**
         * 角色(owner-创建者, member-成员)
         */
        private String role;
    }
} 