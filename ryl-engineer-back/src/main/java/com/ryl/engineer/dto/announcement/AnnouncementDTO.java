package com.ryl.engineer.dto.announcement;

import lombok.Data;

import java.util.Date;

/**
 * 系统公告数据传输对象
 */
@Data
public class AnnouncementDTO {
    
    /**
     * 公告ID
     */
    private Long id;
    
    /**
     * 公告标题
     */
    private String title;
    
    /**
     * 公告内容
     */
    private String content;
    
    /**
     * 发布者信息
     */
    private SenderInfo sender;
    
    /**
     * 重要程度（normal-普通, important-重要, urgent-紧急）
     */
    private String importance;
    
    /**
     * 状态（active-活跃, inactive-非活跃）
     */
    private String status;
    
    /**
     * 生效时间
     */
    private Date startTime;
    
    /**
     * 结束时间
     */
    private Date endTime;
    
    /**
     * 是否弹窗显示
     */
    private Boolean isPopup;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 当前用户是否已读
     */
    private Boolean isRead;
    
    /**
     * 用户阅读时间
     */
    private Date readTime;
    
    /**
     * 发布者信息内部类
     */
    @Data
    public static class SenderInfo {
        /**
         * 发布者ID
         */
        private Long id;
        
        /**
         * 发布者姓名
         */
        private String name;
    }
} 