package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 用户信息响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 工号
     */
    private String workId;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 手机号码
     */
    private String mobile;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 工作地点
     */
    private String location;
    
    /**
     * 头像
     */
    private String avatar;

    /**
     * 技术分类
     */
    private String technicalCategory;

    /**
     * 是否设置了安全密码
     */
    private boolean hasSecurityPassword;

    /**
     * 角色列表
     */
    private java.util.List<String> roles;

    /**
     * 任务统计信息
     */
    private Map<String, Object> taskStats;
} 