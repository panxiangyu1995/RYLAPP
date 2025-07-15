package com.ryl.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 客户实体类
 */
@Data
@TableName("customer")
public class Customer {
    /**
     * 客户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 客户名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 联系人（用作登录用户名）
     */
    @TableField("contact")
    private String contact;
    
    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;
    
    /**
     * 电子邮箱
     */
    private String email;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 加密密码
     */
    private String password;
    
    /**
     * 密码加密盐值
     */
    private String salt;
    
    /**
     * 注册时间
     */
    private Date registerTime;
    
    /**
     * 微信openid
     */
    @TableField("wechat_openid")
    private String openid;
    
    /**
     * 微信昵称
     */
    private String nickname;
    
    /**
     * 微信unionid
     */
    @TableField("wechat_unionid")
    private String unionid;
    
    /**
     * 微信头像
     */
    @TableField("wechat_avatar_url")
    private String avatarUrl;
    
    /**
     * 性别（0未知，1男，2女）
     */
    private Integer gender;
    
    /**
     * 国家
     */
    private String country;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 语言
     */
    private String language;
    
    /**
     * 会话密钥
     */
    private String sessionKey;
    
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
} 