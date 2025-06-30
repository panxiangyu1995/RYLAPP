package com.ryl.engineer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 车辆信息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 车辆ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 车牌号
     */
    private String plateNumber;
    
    /**
     * 车辆类型（公车、私车）
     */
    private String vehicleType;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 