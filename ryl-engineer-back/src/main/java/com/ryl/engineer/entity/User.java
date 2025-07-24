package com.ryl.engineer.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
@ToString
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
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
     * 手机号码
     */
    private String mobile;
    
    /**
     * 密码
     */
    private String password;
    
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
     * 安全密码（二级密码）
     */
    private String securityPassword;

    /**
     * 技术分类
     */
    private String technicalCategory;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;
    
    /**
     * 登录令牌
     */
    private String token;
    
    /**
     * 令牌过期时间
     */
    private Date tokenExpire;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
} 