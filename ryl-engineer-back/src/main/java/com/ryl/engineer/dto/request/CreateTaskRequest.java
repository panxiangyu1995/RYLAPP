package com.ryl.engineer.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建任务请求DTO
 */
@Data
public class CreateTaskRequest {
    /**
     * 任务标题
     */
    @NotBlank(message = "任务标题不能为空")
    @Size(max = 100, message = "任务标题长度不能超过100个字符")
    private String title;
    
    /**
     * 任务类型
     */
    @NotBlank(message = "任务类型不能为空")
    private String taskType;
    
    /**
     * 优先级
     */
    private String priority = "normal";
    
    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    
    /**
     * 是否为系统外任务
     */
    private Boolean isExternalTask = false;
    
    /**
     * 客户对象
     */
    private CustomerRequest customer;
    
    /**
     * 销售人员对象
     */
    private SalesRequest sales;
    
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
     * 描述
     */
    private String description;
    
    /**
     * 数量
     */
    private Integer quantity = 1;
    
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
     * 预约时间（培训任务）
     */
    private LocalDateTime appointmentTime;
    
    /**
     * 图片URL数组
     */
    private List<String> images;
    
    /**
     * 指派的工程师ID数组
     */
    @NotNull(message = "指派的工程师不能为空")
    @Size(min = 1, message = "至少指派一名工程师")
    private List<Long> assignedEngineers;
    
    /**
     * 客户请求对象
     */
    @Data
    public static class CustomerRequest {
        /**
         * 客户ID
         */
        private Long id;
        
        /**
         * 客户名称
         */
        private String name;
        
        /**
         * 客户等级
         */
        private String level;
        
        /**
         * 联系人
         */
        private String contact;
        
        /**
         * 联系电话
         */
        private String phone;
        
        /**
         * 地址
         */
        private String address;
    }
    
    /**
     * 销售人员请求对象
     */
    @Data
    public static class SalesRequest {
        /**
         * 销售人员ID
         */
        private Long id;
    }
} 