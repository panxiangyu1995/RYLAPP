package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 车辆信息请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInfoRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 车牌号
     */
    @NotBlank(message = "车牌号不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5][A-Z][A-Z0-9]{5}$", message = "车牌号格式不正确，如：粤A12345")
    private String plateNumber;
    
    /**
     * 车辆类型
     */
    @NotBlank(message = "车辆类型不能为空")
    private String vehicleType;
} 