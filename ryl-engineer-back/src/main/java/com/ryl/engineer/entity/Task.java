package com.ryl.engineer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务实体类
 */
@Data
@TableName("task")
public class Task {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务编号（格式：类型缩写-年份-序号，如RP-2024-001）
     */
    private String taskId;
    
    /**
     * 任务标题
     */
    private String title;
    
    /**
     * 任务类型（repair/maintenance/recycle/leasing/training/verification/selection/installation）
     */
    private String taskType;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 客户名称
     */
    private String customer;
    
    /**
     * 客户级别
     */
    private String customerLevel;
    
    /**
     * 地址
     */
    private String location;
    
    /**
     * 联系人
     */
    private String contactPerson;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 销售人员ID
     */
    private Long salesId;

    /**
     * 工程师ID
     */
    @TableField(exist = false)
    private Integer engineerId;

    /**
     * 工程师姓名
     */
    @TableField(exist = false)
    private String engineerName;
    
    /**
     * 优先级（low/normal/high）
     */
    private String priority;
    
    /**
     * 任务状态（draft/pending/in-progress/completed/cancelled）
     */
    private String status;
    
    /**
     * 是否为系统外任务（0-否，1-是）
     */
    private Integer isExternalTask;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 开始日期
     */
    private LocalDateTime startDate;
    
    /**
     * 截止日期
     */
    private LocalDateTime deadline;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedDate;
    
    /**
     * 设备名称
     */
    private String deviceName;
    
    /**
     * 设备型号
     */
    private String deviceModel;
    
    /**
     * 设备品牌
     */
    private String deviceBrand;

    /**
     * 仪器类型
     */
    private String deviceType;

    /**
     * 设备序列号
     */
    private String deviceSn;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 故障描述
     */
    private String faultDescription;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 附件信息
     */
    private String attachments;
    
    /**
     * 验证类别（IQ/OQ/PQ）
     */
    private String verificationType;
    
    /**
     * 用途（选型任务）
     */
    private String purpose;
    
    /**
     * 需求描述（选型任务）
     */
    private String requirementDescription;
    
    /**
     * 预约时间（培训任务）
     */
    private LocalDateTime appointmentTime;
    
    /**
     * 当前步骤
     */
    private Integer currentStep;
    
    /**
     * 上一个有效步骤索引
     */
    private Integer lastValidStep;
    
    /**
     * 预算金额
     */
    private BigDecimal budget;
    
    /**
     * 实际价格
     */
    private BigDecimal price;
    
    /**
     * 是否需要上门（0-不需要，1-需要）
     */
    private Integer needSiteVisit;
    
    /**
     * 约定上门时间
     */
    private LocalDateTime visitAppointmentTime;
    
    /**
     * 客户是否已确认报价（0-未确认，1-已确认）
     */
    private Integer priceConfirmed;
} 