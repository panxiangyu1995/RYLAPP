package com.ryl.engineer.service.impl;

import com.ryl.engineer.dto.VehicleRecordRequest;
import com.ryl.engineer.dto.VehicleRecordResponse;
import com.ryl.engineer.dto.VehicleRecordStatsResponse;
import com.ryl.engineer.entity.VehicleRecord;
import com.ryl.engineer.mapper.VehicleRecordMapper;
import com.ryl.engineer.service.VehicleRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 车辆打卡记录服务实现类
 */
@Service
public class VehicleRecordServiceImpl implements VehicleRecordService {

    @Autowired
    private VehicleRecordMapper vehicleRecordMapper;

    @Override
    @Transactional
    public Long createRecord(Long userId, VehicleRecordRequest request) {
        VehicleRecord record = new VehicleRecord();
        record.setUserId(userId);
        record.setTaskId(request.getTaskId());
        record.setTaskName(request.getTaskName());
        record.setType(request.getType());
        record.setCheckInTime(new Date()); // 使用当前时间作为打卡时间
        record.setLocation(request.getLocation());
        record.setLongitude(request.getLongitude());
        record.setLatitude(request.getLatitude());
        record.setPhotos(request.getPhotos());
        record.setDistance(request.getDistance());
        record.setTransportType(request.getTransportType());
        record.setRemark(request.getRemark());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        
        vehicleRecordMapper.insert(record);
        return record.getId();
    }

    @Override
    public VehicleRecordResponse getRecordById(Long id) {
        VehicleRecord record = vehicleRecordMapper.selectById(id);
        if (record == null) {
            return null;
        }
        return convertToResponse(record);
    }

    @Override
    public List<VehicleRecordResponse> getRecordsByUserId(Long userId) {
        List<VehicleRecord> records = vehicleRecordMapper.selectByUserId(userId);
        return convertToResponseList(records);
    }

    @Override
    public List<VehicleRecordResponse> getRecordsByTaskId(String taskId) {
        List<VehicleRecord> records = vehicleRecordMapper.selectByTaskId(taskId);
        return convertToResponseList(records);
    }

    @Override
    public List<VehicleRecordResponse> getRecordsByTimeRange(Long userId, Date startTime, Date endTime) {
        List<VehicleRecord> records = vehicleRecordMapper.selectByUserIdAndTimeRange(userId, startTime, endTime);
        return convertToResponseList(records);
    }

    @Override
    public VehicleRecordStatsResponse getMonthlyStats(Long userId, int year, int month) {
        // 计算月份的开始和结束时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1, 0, 0, 0); // 月份从0开始，所以减1
        calendar.set(Calendar.MILLISECOND, 0);
        Date startTime = calendar.getTime();
        
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        Date endTime = calendar.getTime();
        
        // 获取统计数据
        int checkInCount = vehicleRecordMapper.countByUserIdAndTimeRange(userId, startTime, endTime);
        int taskCount = vehicleRecordMapper.countTasksByUserIdAndTimeRange(userId, startTime, endTime);
        double totalDistance = vehicleRecordMapper.sumDistanceByUserIdAndTimeRange(userId, startTime, endTime);
        
        // 构建响应
        VehicleRecordStatsResponse response = new VehicleRecordStatsResponse();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        response.setMonth(sdf.format(startTime));
        response.setCheckInCount(checkInCount);
        response.setTaskCount(taskCount);
        response.setTotalDistance(totalDistance);
        
        return response;
    }

    @Override
    public VehicleRecordStatsResponse getCurrentMonthStats(Long userId) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，所以加1
        
        return getMonthlyStats(userId, year, month);
    }
    
    /**
     * 将实体对象转换为响应DTO
     *
     * @param record 打卡记录实体
     * @return 打卡记录响应DTO
     */
    private VehicleRecordResponse convertToResponse(VehicleRecord record) {
        if (record == null) {
            return null;
        }
        
        VehicleRecordResponse response = new VehicleRecordResponse();
        response.setId(record.getId());
        response.setTaskId(record.getTaskId());
        response.setTaskName(record.getTaskName());
        response.setType(record.getType());
        response.setTypeName(getTypeNameByCode(record.getType()));
        response.setCheckInTime(record.getCheckInTime());
        response.setLocation(record.getLocation());
        response.setLongitude(record.getLongitude());
        response.setLatitude(record.getLatitude());
        response.setPhotos(record.getPhotos());
        
        // 计算照片数量
        if (record.getPhotos() != null && !record.getPhotos().isEmpty()) {
            String[] photoArray = record.getPhotos().split(",");
            response.setPhotoCount(photoArray.length);
        } else {
            response.setPhotoCount(0);
        }
        
        response.setDistance(record.getDistance());
        response.setTransportType(record.getTransportType());
        response.setTransportTypeName(getTransportTypeNameByCode(record.getTransportType()));
        response.setRemark(record.getRemark());
        response.setCreateTime(record.getCreateTime());
        
        return response;
    }
    
    /**
     * 将实体对象列表转换为响应DTO列表
     *
     * @param records 打卡记录实体列表
     * @return 打卡记录响应DTO列表
     */
    private List<VehicleRecordResponse> convertToResponseList(List<VehicleRecord> records) {
        List<VehicleRecordResponse> responseList = new ArrayList<>();
        if (records != null && !records.isEmpty()) {
            for (VehicleRecord record : records) {
                VehicleRecordResponse response = convertToResponse(record);
                if (response != null) {
                    responseList.add(response);
                }
            }
        }
        return responseList;
    }
    
    /**
     * 根据类型代码获取类型名称
     *
     * @param typeCode 类型代码
     * @return 类型名称
     */
    private String getTypeNameByCode(Integer typeCode) {
        if (typeCode == null) {
            return "未知";
        }
        
        switch (typeCode) {
            case 1:
                return "出发打卡";
            case 2:
                return "到达打卡";
            case 3:
                return "返程打卡";
            default:
                return "其他";
        }
    }
    
    /**
     * 根据交通工具类型代码获取类型名称
     *
     * @param transportTypeCode 交通工具类型代码
     * @return 交通工具类型名称
     */
    private String getTransportTypeNameByCode(String transportTypeCode) {
        if (transportTypeCode == null) {
            return "未知";
        }
        
        switch (transportTypeCode) {
            case "company":
                return "公车";
            case "private":
                return "私车";
            case "public":
                return "公共交通";
            default:
                return "其他";
        }
    }
} 