package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工程师数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EngineerDTO {
    /**
     * 工程师ID
     */
    private Long id;
    
    /**
     * 工程师姓名
     */
    private String name;
    
    /**
     * 工程师头像
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
     * 是否任务负责人
     */
    private Boolean isOwner;
    
    /**
     * 是否在线
     */
    private Boolean online;
} 