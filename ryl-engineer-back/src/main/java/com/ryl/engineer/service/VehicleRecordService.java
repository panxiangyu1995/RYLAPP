package com.ryl.engineer.service;

import com.ryl.engineer.dto.VehicleRecordRequest;
import com.ryl.engineer.dto.VehicleRecordResponse;
import com.ryl.engineer.dto.VehicleRecordStatsResponse;
import com.ryl.engineer.entity.VehicleRecord;

import java.util.Date;
import java.util.List;

/**
 * 车辆打卡记录服务接口
 */
public interface VehicleRecordService {
    
    /**
     * 创建打卡记录
     *
     * @param userId  用户ID
     * @param request 打卡记录请求
     * @return 打卡记录ID
     */
    Long createRecord(Long userId, VehicleRecordRequest request);
    
    /**
     * 根据ID获取打卡记录
     *
     * @param id 记录ID
     * @return 打卡记录响应
     */
    VehicleRecordResponse getRecordById(Long id);
    
    /**
     * 获取用户的打卡记录列表
     *
     * @param userId 用户ID
     * @return 打卡记录响应列表
     */
    List<VehicleRecordResponse> getRecordsByUserId(Long userId);
    
    /**
     * 获取任务的打卡记录列表
     *
     * @param taskId 任务ID
     * @return 打卡记录响应列表
     */
    List<VehicleRecordResponse> getRecordsByTaskId(String taskId);
    
    /**
     * 获取用户在指定时间范围内的打卡记录列表
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 打卡记录响应列表
     */
    List<VehicleRecordResponse> getRecordsByTimeRange(Long userId, Date startTime, Date endTime);
    
    /**
     * 获取用户在指定月份的打卡统计信息
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 打卡统计响应
     */
    VehicleRecordStatsResponse getMonthlyStats(Long userId, int year, int month);
    
    /**
     * 获取用户的当月打卡统计信息
     *
     * @param userId 用户ID
     * @return 打卡统计响应
     */
    VehicleRecordStatsResponse getCurrentMonthStats(Long userId);
} 