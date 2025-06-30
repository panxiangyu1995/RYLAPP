package com.ryl.engineer.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天会话成员实体类
 * 对应表：chat_conversation_member
 */
@Data
public class ChatConversationMember implements Serializable {
    
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
     * 用户ID
     */
    private Long userId;
    
    /**
     * 角色（owner-创建者, member-成员）
     */
    private String role;
    
    /**
     * 未读消息数
     */
    private Integer unreadCount;
    
    /**
     * 是否静音（0-否，1-是）
     */
    private Integer isMute;
    
    /**
     * 是否置顶（0-否，1-是）
     */
    private Integer isSticky;
    
    /**
     * 加入时间
     */
    private Date joinTime;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 