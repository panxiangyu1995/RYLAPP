package com.ryl.engineer.service;

import com.ryl.engineer.dto.VehicleInfoRequest;
import com.ryl.engineer.dto.VehicleInfoResponse;
import com.ryl.engineer.entity.VehicleInfo;

import java.util.List;

/**
 * 车辆信息服务接口
 */
public interface VehicleInfoService {
    
    /**
     * 获取用户车辆信息
     *
     * @param userId 用户ID
     * @return 车辆信息响应
     */
    VehicleInfoResponse getUserVehicle(Long userId);
    
    /**
     * 更新用户车辆信息
     *
     * @param userId  用户ID
     * @param request 车辆信息请求
     * @return 更新后的车辆信息，失败返回null
     */
    VehicleInfo updateVehicle(Long userId, VehicleInfoRequest request);
    
    /**
     * 获取用户所有车辆信息
     *
     * @param userId 用户ID
     * @return 车辆信息列表
     */
    List<VehicleInfo> getUserVehicles(Long userId);
    
    /**
     * 获取用户所有车辆信息的响应对象
     *
     * @param userId 用户ID
     * @return 车辆信息响应列表
     */
    List<VehicleInfoResponse> getUserVehicleResponses(Long userId);
    
    /**
     * 获取指定ID的车辆信息
     *
     * @param id     车辆ID
     * @param userId 用户ID
     * @return 车辆信息响应
     */
    VehicleInfoResponse getVehicleById(Long id, Long userId);
    
    /**
     * 创建用户车辆信息
     *
     * @param userId  用户ID
     * @param request 车辆信息请求
     * @return 创建的车辆信息，失败返回null
     */
    VehicleInfo createVehicle(Long userId, VehicleInfoRequest request);
    
    /**
     * 删除用户车辆信息
     *
     * @param id     车辆ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteVehicle(Long id, Long userId);
} 