package com.ryl.engineer.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统公告实体类
 * 对应表：system_announcement
 */
@Data
@TableName("system_announcement")
public class SystemAnnouncement implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 公告标题
     */
    @TableField("title")
    private String title;
    
    /**
     * 公告内容
     */
    @TableField("content")
    private String content;
    
    /**
     * 发布人ID
     */
    @TableField("sender_id")
    private Long senderId;
    
    /**
     * 发布人姓名
     */
    @TableField("sender_name")
    private String senderName;
    
    /**
     * 重要程度（normal-普通, important-重要, urgent-紧急）
     */
    @TableField("importance")
    private String importance;
    
    /**
     * 状态（active-活跃, inactive-非活跃）
     */
    @TableField("status")
    private String status;
    
    /**
     * 生效时间
     */
    @TableField("start_time")
    private Date startTime;
    
    /**
     * 结束时间（NULL表示永久有效）
     */
    @TableField("end_time")
    private Date endTime;
    
    /**
     * 是否弹窗显示（0-否, 1-是）
     */
    @TableField("is_popup")
    private Integer isPopup;
    
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