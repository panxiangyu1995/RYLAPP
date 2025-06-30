package com.ryl.engineer.dto.assistance;

import lombok.Data;

import java.util.Date;

/**
 * 协助请求数据传输对象
 */
@Data
public class AssistanceRequestDTO {
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 请求者信息
     */
    private UserInfo requester;
    
    /**
     * 工程师信息
     */
    private UserInfo engineer;
    
    /**
     * 相关任务ID
     */
    private String taskId;
    
    /**
     * 相关任务标题
     */
    private String taskTitle;
    
    /**
     * 紧急程度（low-低, medium-中, high-高）
     */
    private String urgency;
    
    /**
     * 状态（pending-待处理, accepted-已接受, rejected-已拒绝, cancelled-已取消, completed-已完成）
     */
    private String status;
    
    /**
     * 问题描述
     */
    private String description;
    
    /**
     * 回复内容
     */
    private String response;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 处理时间
     */
    private Date handleTime;
    
    /**
     * 关联的会话ID
     */
    private String conversationId;
    
    /**
     * 用户信息内部类
     */
    @Data
    public static class UserInfo {
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
    }
} 