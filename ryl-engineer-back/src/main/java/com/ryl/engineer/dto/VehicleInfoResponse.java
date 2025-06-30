package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 车辆信息响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInfoResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 车辆ID
     */
    private String vehicleId;
    
    /**
     * 车牌号
     */
    private String plateNumber;
    
    /**
     * 车辆类型
     */
    private String vehicleType;
} 