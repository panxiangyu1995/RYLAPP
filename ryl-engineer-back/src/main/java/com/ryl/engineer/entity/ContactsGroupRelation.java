package com.ryl.engineer.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 联系人分组关系实体类
 * 对应表：contacts_group_relation
 */
@Data
public class ContactsGroupRelation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 分组ID
     */
    private Long groupId;
    
    /**
     * 联系人ID
     */
    private Long contactId;
    
    /**
     * 创建时间
     */
    private Date createTime;
} 