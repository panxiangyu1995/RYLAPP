package com.ryl.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("notification_log")
public class NotificationLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private String taskId;

    @TableField("customer_id")
    private Long customerId;

    @TableField("template_id")
    private String templateId;

    private String status;

    @TableField("response_code")
    private String responseCode;

    @TableField("response_message")
    private String responseMessage;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
} 