package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.VehicleInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车辆信息Mapper接口
 */
@Mapper
public interface VehicleInfoMapper {
    
    /**
     * 根据用户ID查询车辆信息
     *
     * @param userId 用户ID
     * @return 车辆信息列表
     */
    List<VehicleInfo> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据ID查询车辆信息
     *
     * @param id 车辆ID
     * @return 车辆信息
     */
    VehicleInfo selectById(@Param("id") Long id);
    
    /**
     * 新增车辆信息
     *
     * @param vehicleInfo 车辆信息
     * @return 影响行数
     */
    int insert(VehicleInfo vehicleInfo);
    
    /**
     * 修改车辆信息
     *
     * @param vehicleInfo 车辆信息
     * @return 影响行数
     */
    int update(VehicleInfo vehicleInfo);
    
    /**
     * 删除车辆信息
     *
     * @param id 车辆ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
} 