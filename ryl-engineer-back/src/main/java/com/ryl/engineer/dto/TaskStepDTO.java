package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 任务步骤数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStepDTO {
    /**
     * 数据库中的步骤ID
     */
    private Long id;

    /**
     * 步骤的业务标识符，用于前端识别特殊步骤
     */
    private String stepKey;

    /**
     * 步骤索引
     */
    private Integer index;
    
    /**
     * 步骤名称
     */
    private String name;
    
    /**
     * 步骤状态
     */
    private String status;
    
    /**
     * 步骤开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 步骤完成时间
     */
    private LocalDateTime endTime;
    
    /**
     * 操作人信息
     */
    private OperatorDTO operator;

    /**
     * 关联到此步骤的所有工作记录
     */
    private List<StepRecordDTO> records;
    
    /**
     * 步骤记录内容
     */
    private String recordContent;
    
    /**
     * 步骤结果
     */
    private String result;
    
    /**
     * 步骤相关图片URL数组
     */
    private List<String> images;

    /**
     * 步骤相关非图片附件列表
     */
    private List<AttachmentDTO> attachments;
    
    /**
     * 步骤表单数据
     */
    private Map<String, Object> formData;
    
    /**
     * 可执行操作
     */
    private List<String> actions;
    
    /**
     * 操作人DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperatorDTO {
        /**
         * 操作人ID
         */
        private Long id;
        
        /**
         * 操作人姓名
         */
        private String name;
        
        /**
         * 操作人头像
         */
        private String avatar;
    }
} 