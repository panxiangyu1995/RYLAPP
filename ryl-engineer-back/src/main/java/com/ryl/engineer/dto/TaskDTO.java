package com.ryl.engineer.dto;

import com.ryl.engineer.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 任务标题
     */
    private String title;
    
    /**
     * 任务类型
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
     * 优先级
     */
    private String priority;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 任务状态文本
     */
    private String statusText;
    
    /**
     * 是否为系统外任务
     */
    private Boolean isExternalTask;
    
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
     * 验证类别
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
     * 是否需要上门
     */
    private Boolean needSiteVisit;
    
    /**
     * 任务图片URL列表
     */
    private List<String> images;
    
    /**
     * 工程师列表
     */
    private List<EngineerDTO> engineers;
} 