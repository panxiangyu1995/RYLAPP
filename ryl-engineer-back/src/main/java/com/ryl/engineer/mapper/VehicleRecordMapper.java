package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.VehicleRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 车辆打卡记录Mapper接口
 */
@Mapper
public interface VehicleRecordMapper {
    
    /**
     * 新增打卡记录
     *
     * @param record 打卡记录
     * @return 影响行数
     */
    int insert(VehicleRecord record);
    
    /**
     * 根据ID查询打卡记录
     *
     * @param id 记录ID
     * @return 打卡记录
     */
    VehicleRecord selectById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询打卡记录列表
     *
     * @param userId 用户ID
     * @return 打卡记录列表
     */
    List<VehicleRecord> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据任务ID查询打卡记录列表
     *
     * @param taskId 任务ID
     * @return 打卡记录列表
     */
    List<VehicleRecord> selectByTaskId(@Param("taskId") String taskId);
    
    /**
     * 根据用户ID和时间范围查询打卡记录列表
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 打卡记录列表
     */
    List<VehicleRecord> selectByUserIdAndTimeRange(
            @Param("userId") Long userId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);
    
    /**
     * 统计用户在指定时间范围内的打卡次数
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 打卡次数
     */
    int countByUserIdAndTimeRange(
            @Param("userId") Long userId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);
    
    /**
     * 统计用户在指定时间范围内的任务数
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 任务数
     */
    int countTasksByUserIdAndTimeRange(
            @Param("userId") Long userId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);
    
    /**
     * 统计用户在指定时间范围内的总里程
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 总里程
     */
    double sumDistanceByUserIdAndTimeRange(
            @Param("userId") Long userId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);
} 