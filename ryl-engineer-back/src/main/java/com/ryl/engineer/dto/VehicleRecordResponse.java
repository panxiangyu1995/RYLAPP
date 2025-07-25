package com.ryl.engineer.dto;

import lombok.Data;

import java.util.Date;

/**
 * 车辆打卡记录响应DTO
 */
@Data
public class VehicleRecordResponse {
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 打卡类型（1-出发打卡，2-到达打卡，3-返程打卡）
     */
    private Integer type;
    
    /**
     * 打卡类型名称
     */
    private String typeName;
    
    /**
     * 打卡时间
     */
    private Date checkInTime;
    
    /**
     * 打卡地址
     */
    private String location;
    
    /**
     * 经度
     */
    private String longitude;
    
    /**
     * 纬度
     */
    private String latitude;
    
    /**
     * 照片URL，多个照片以逗号分隔
     */
    private String photos;
    
    /**
     * 照片数量
     */
    private Integer photoCount;
    
    /**
     * 里程数（公里）
     */
    private Double distance;
    
    /**
     * 交通工具类型（company-公车, private-私车, public-公共交通）
     */
    private String transportType;
    
    /**
     * 交通工具类型名称
     */
    private String transportTypeName;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private Date createTime;
} 