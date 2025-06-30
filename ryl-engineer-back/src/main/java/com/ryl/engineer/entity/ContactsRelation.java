package com.ryl.engineer.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 联系人关系实体类
 * 对应表：contacts_relation
 */
@Data
public class ContactsRelation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 联系人ID
     */
    private Long contactId;
    
    /**
     * 关系类型（colleague-同事, customer-客户, supplier-供应商）
     */
    private String relationType;
    
    /**
     * 是否收藏（0-否，1-是）
     */
    private Integer favorite;
    
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
} 