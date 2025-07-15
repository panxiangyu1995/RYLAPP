package com.ryl.miniprogram.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 设备信息数据传输对象
 */
@Data
public class DeviceDTO {
    /**
     * 设备名称
     */
    private String name;
    
    /**
     * 设备类型
     */
    private String type;
    
    /**
     * 设备品牌
     */
    private String brand;
    
    /**
     * 设备型号
     */
    private String model;
    
    // 移除故障描述字段，使用任务的content字段代替
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 附件信息
     */
    private String accessories;
    
    /**
     * 预约时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date appointmentTime;
    
    /**
     * 验证类别
     */
    private String verificationType;
    
    /**
     * 用途
     */
    private String purpose;
    
    // 移除需求描述字段，使用任务的content字段代替
}