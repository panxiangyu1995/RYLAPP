package com.ryl.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 任务实体类
 */
@Data
@TableName("task")
public class Task {
    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务编号
     */
    @TableField("task_id")
    private String taskId;
    
    /**
     * 任务标题
     */
    private String title;
    
    /**
     * 任务描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 任务类型（repair：维修，maintenance：保养，recycle：回收，leasing：租赁，
     * training：培训，verification：检测，selection：选型，installation：安装）
     */
    @TableField("task_type")
    private String taskType;
    
    /**
     * 任务状态（draft/pending/in-progress/completed/cancelled）
     */
    @TableField("status")
    private String status;
    
    /**
     * 优先级（low/normal/high）
     */
    private String priority;
    
    /**
     * 开始时间
     */
    @TableField("start_date")
    private Date startDate;
    
    /**
     * 结束时间
     */
    @TableField("deadline")
    private Date deadline;
    
    /**
     * 完成时间
     */
    @TableField("completed_date")
    private Date completedDate;
    
    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Long customerId;
    
    /**
     * 客户名称
     */
    @TableField("customer")
    private String customerName;
    
    /**
     * 客户级别（A/B/C）
     */
    @TableField("customer_level")
    private String customerLevel;
    
    /**
     * 联系人
     */
    @TableField("contact_person")
    private String contactPerson;
    
    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;
    
    /**
     * 地址
     */
    @TableField("location")
    private String location;
    
    /**
     * 设备名称
     */
    @TableField("device_name")
    private String deviceName;
    
    /**
     * 设备型号
     */
    @TableField("device_model")
    private String deviceModel;
    
    /**
     * 设备品牌
     */
    @TableField("device_brand")
    private String deviceBrand;
    
    /**
     * 设备序列号
     */
    @TableField("device_sn")
    private String deviceSn;
    
    // 描述字段已移除，统一使用description字段
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 附件信息
     */
    private String attachments;
    
    /**
     * 验证类别
     */
    @TableField("verification_type")
    private String verificationType;
    
    /**
     * 用途
     */
    private String purpose;
    
    // 需求描述字段已移除，统一使用description字段
    
    /**
     * 预约时间
     */
    @TableField("appointment_time")
    private Date appointmentTime;
    
    /**
     * 当前步骤
     */
    @TableField("current_step")
    private Integer currentStep;
    
    /**
     * 实际价格
     */
    @TableField("price")
    private BigDecimal price;
    
    /**
     * 客户是否已确认报价（0-未确认，1-已确认）
     */
    @TableField("price_confirmed")
    private Integer priceConfirmed;
    
    /**
     * 创建人ID（系统中记录，不映射到数据库）
     */
    @TableField(exist = false)
    private Long createUserId;
    
    /**
     * 负责人ID（系统中记录，不映射到数据库）
     */
    @TableField(exist = false)
    private Long assigneeId;

    /**
     * 工程师姓名（负责人）
     */
    @TableField(exist = false)
    private String engineerName;

    /**
     * 工程师电话
     */
    @TableField(exist = false)
    private String engineerPhone;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @TableField(exist = false)
    private Date updateTime;
} 