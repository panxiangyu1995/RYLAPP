package com.ryl.engineer.entity;

import java.util.Date;
import lombok.Data;

/**
 * 工程师-客户关系实体类
 */
@Data
public class CustomerEngineerRelation {
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 工程师ID
     */
    private Long engineerId;
    
    /**
     * 关系类型
     */
    private String relationType;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 