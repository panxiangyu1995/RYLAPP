package com.ryl.engineer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 车辆打卡记录请求DTO
 */
@Data
public class VehicleRecordRequest {
    
    /**
     * 任务ID
     */
    @NotBlank(message = "任务ID不能为空")
    private String taskId;
    
    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    private String taskName;
    
    /**
     * 打卡类型（1-出发打卡，2-到达打卡，3-返程打卡）
     */
    @NotNull(message = "打卡类型不能为空")
    private Integer type;
    
    /**
     * 打卡地址
     */
    @NotBlank(message = "打卡地址不能为空")
    private String location;
    
    /**
     * 经度
     */
    @NotBlank(message = "经度不能为空")
    private String longitude;
    
    /**
     * 纬度
     */
    @NotBlank(message = "纬度不能为空")
    private String latitude;
    
    /**
     * 照片URL，多个照片以逗号分隔
     */
    private String photos;
    
    /**
     * 里程数（公里）
     */
    private Double distance;
    
    /**
     * 交通工具类型（company-公车, private-私车, public-公共交通）
     */
    @NotBlank(message = "交通工具类型不能为空")
    private String transportType;
    
    /**
     * 备注
     */
    private String remark;
} 