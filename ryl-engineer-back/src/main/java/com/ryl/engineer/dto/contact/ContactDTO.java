package com.ryl.engineer.dto.contact;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 联系人数据传输对象
 */
@Data
public class ContactDTO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 工号
     */
    private String workId;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 手机号
     */
    private String mobile;
    
    /**
     * 状态（0-离线，1-在线）
     */
    private Integer status;
    
    /**
     * 最后活跃时间
     */
    private Date lastActiveTime;
    
    /**
     * 角色
     */
    private String role;
    
    /**
     * 联系人关系类型（colleague-同事, customer-客户, supplier-供应商）
     */
    private String relationType;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 当前任务列表
     */
    private List<TaskInfo> currentTasks;
    
    /**
     * 任务信息内部类
     */
    @Data
    public static class TaskInfo {
        /**
         * 任务ID
         */
        private String taskId;
        
        /**
         * 任务标题
         */
        private String title;
        
        /**
         * 任务状态
         */
        private String status;
    }
} 