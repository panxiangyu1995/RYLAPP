package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务流程数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskFlowDTO {
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
     * 任务状态
     */
    private String status;
    
    /**
     * 当前步骤
     */
    private Integer currentStep;
    
    /**
     * 总步骤数
     */
    private Integer totalSteps;
    
    /**
     * 步骤数组
     */
    private List<TaskStepDTO> steps;
    
    /**
     * 任务时间线
     */
    private List<TimelineItem> timeline;
    
    /**
     * 时间线项目
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineItem {
        /**
         * 时间
         */
        private LocalDateTime time;
        
        /**
         * 操作
         */
        private String action;
        
        /**
         * 操作人
         */
        private String operator;
        
        /**
         * 内容
         */
        private String content;
    }

    /**
     * 任务流程记录DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowRecordDTO {
        /**
         * 记录ID
         */
        private Long id;
        
        /**
         * 任务ID
         */
        private String taskId;
        
        /**
         * 步骤ID
         */
        private Long stepId;
        
        /**
         * 步骤索引
         */
        private Integer stepIndex;
        
        /**
         * 记录描述
         */
        private String description;
        
        /**
         * 创建人ID
         */
        private Long creatorId;
        
        /**
         * 创建人姓名
         */
        private String creatorName;
        
        /**
         * 创建时间
         */
        private LocalDateTime createTime;
        
        /**
         * 更新时间
         */
        private LocalDateTime updateTime;
        
        /**
         * 图片列表
         */
        private List<String> images;
        
        /**
         * 附件列表
         */
        private List<FileDTO> files;
    }
    
    /**
     * 文件DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileDTO {
        /**
         * 文件名
         */
        private String fileName;
        
        /**
         * 文件URL
         */
        private String fileUrl;
        
        /**
         * 文件类型
         */
        private String fileType;
        
        /**
         * 文件大小(字节)
         */
        private Long fileSize;
    }
} 