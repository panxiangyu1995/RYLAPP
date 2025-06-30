package com.ryl.engineer.service.impl;

import com.ryl.engineer.dto.VehicleInfoRequest;
import com.ryl.engineer.dto.VehicleInfoResponse;
import com.ryl.engineer.entity.VehicleInfo;
import com.ryl.engineer.mapper.VehicleInfoMapper;
import com.ryl.engineer.service.VehicleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 车辆信息服务实现类
 */
@Service
public class VehicleInfoServiceImpl implements VehicleInfoService {

    @Autowired
    private VehicleInfoMapper vehicleInfoMapper;

    @Override
    public VehicleInfoResponse getUserVehicle(Long userId) {
        // 获取用户车辆信息列表
        List<VehicleInfo> vehicleInfoList = vehicleInfoMapper.selectByUserId(userId);
        
        // 如果用户没有车辆信息，返回null
        if (vehicleInfoList == null || vehicleInfoList.isEmpty()) {
            return null;
        }
        
        // 取第一条车辆信息
        VehicleInfo vehicleInfo = vehicleInfoList.get(0);
        
        // 构建响应
        return convertToResponse(vehicleInfo);
    }

    @Override
    @Transactional
    public VehicleInfo updateVehicle(Long userId, VehicleInfoRequest request) {
        // 获取用户车辆信息列表
        List<VehicleInfo> vehicleInfoList = vehicleInfoMapper.selectByUserId(userId);
        
        // 如果用户已有车辆信息，更新第一条
        if (vehicleInfoList != null && !vehicleInfoList.isEmpty()) {
            VehicleInfo vehicleInfo = vehicleInfoList.get(0);
            vehicleInfo.setPlateNumber(request.getPlateNumber());
            vehicleInfo.setVehicleType(request.getVehicleType());
            vehicleInfo.setUpdateTime(new Date());
            
            if (vehicleInfoMapper.update(vehicleInfo) > 0) {
                return vehicleInfo;
            }
            return null;
        }
        
        // 如果用户没有车辆信息，创建新的
        VehicleInfo vehicleInfo = new VehicleInfo();
        vehicleInfo.setUserId(userId);
        vehicleInfo.setPlateNumber(request.getPlateNumber());
        vehicleInfo.setVehicleType(request.getVehicleType());
        vehicleInfo.setCreateTime(new Date());
        vehicleInfo.setUpdateTime(new Date());
        
        if (vehicleInfoMapper.insert(vehicleInfo) > 0) {
            return vehicleInfo;
        }
        return null;
    }

    @Override
    public List<VehicleInfo> getUserVehicles(Long userId) {
        return vehicleInfoMapper.selectByUserId(userId);
    }
    
    @Override
    public List<VehicleInfoResponse> getUserVehicleResponses(Long userId) {
        List<VehicleInfo> vehicleInfoList = vehicleInfoMapper.selectByUserId(userId);
        if (vehicleInfoList == null || vehicleInfoList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return vehicleInfoList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public VehicleInfoResponse getVehicleById(Long id, Long userId) {
        VehicleInfo vehicleInfo = vehicleInfoMapper.selectById(id);
        if (vehicleInfo == null || !vehicleInfo.getUserId().equals(userId)) {
            return null;
        }
        
        return convertToResponse(vehicleInfo);
    }
    
    @Override
    @Transactional
    public VehicleInfo createVehicle(Long userId, VehicleInfoRequest request) {
        VehicleInfo vehicleInfo = new VehicleInfo();
        vehicleInfo.setUserId(userId);
        vehicleInfo.setPlateNumber(request.getPlateNumber());
        vehicleInfo.setVehicleType(request.getVehicleType());
        vehicleInfo.setCreateTime(new Date());
        vehicleInfo.setUpdateTime(new Date());
        
        if (vehicleInfoMapper.insert(vehicleInfo) > 0) {
            return vehicleInfo;
        }
        return null;
    }
    
    @Override
    @Transactional
    public boolean deleteVehicle(Long id, Long userId) {
        VehicleInfo vehicleInfo = vehicleInfoMapper.selectById(id);
        if (vehicleInfo == null || !vehicleInfo.getUserId().equals(userId)) {
            return false;
        }
        
        return vehicleInfoMapper.deleteById(id) > 0;
    }
    
    /**
     * 将VehicleInfo实体转换为VehicleInfoResponse
     *
     * @param vehicleInfo 车辆信息实体
     * @return 车辆信息响应
     */
    private VehicleInfoResponse convertToResponse(VehicleInfo vehicleInfo) {
        if (vehicleInfo == null) {
            return null;
        }
        
        VehicleInfoResponse response = new VehicleInfoResponse();
        response.setVehicleId(String.valueOf(vehicleInfo.getId()));
        response.setPlateNumber(vehicleInfo.getPlateNumber());
        response.setVehicleType(vehicleInfo.getVehicleType());
        
        return response;
    }
} 