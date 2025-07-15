package com.ryl.miniprogram.converter;

import com.ryl.miniprogram.dto.DeviceDTO;
import org.springframework.stereotype.Component;

/**
 * 设备数据转换器
 * 注意：由于设备信息没有对应的实体类，此转换器主要用于处理设备信息的存储和检索
 */
@Component
public class DeviceConverter {
    
    /**
     * 将设备信息转换为 JSON 字符串
     *
     * @param deviceDTO 设备 DTO
     * @return JSON 字符串
     */
    public String toJsonString(DeviceDTO deviceDTO) {
        if (deviceDTO == null) {
            return null;
        }
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return objectMapper.writeValueAsString(deviceDTO);
        } catch (Exception e) {
            throw new RuntimeException("设备信息转换为 JSON 失败", e);
        }
    }
    
    /**
     * 将 JSON 字符串转换为设备信息
     *
     * @param jsonString JSON 字符串
     * @return 设备 DTO
     */
    public DeviceDTO fromJsonString(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return objectMapper.readValue(jsonString, DeviceDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON 转换为设备信息失败", e);
        }
    }
}