package com.ryl.engineer.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;

/**
 * 公告已读状态实体类
 * 对应表：announcement_read
 */
@Data
@TableName("announcement_read")
public class AnnouncementRead implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 公告ID
     */
    @TableField("announcement_id")
    private Long announcementId;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 已读时间
     */
    @TableField("read_time")
    private Date readTime;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
} 