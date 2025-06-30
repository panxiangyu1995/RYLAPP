package com.ryl.engineer.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;

/**
 * 协助请求接收者关系实体类
 * 对应表：assistance_request_recipient
 */
@Data
@TableName("assistance_request_recipient")
public class AssistanceRequestRecipient implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 协助请求ID
     */
    @TableField("request_id")
    private Long requestId;
    
    /**
     * 接收者用户ID
     */
    @TableField("recipient_id")
    private Long recipientId;
    
    /**
     * 已读状态（0-未读，1-已读）
     */
    @TableField("is_read")
    private Integer isRead;
    
    /**
     * 读取时间
     */
    @TableField("read_time")
    private Date readTime;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 