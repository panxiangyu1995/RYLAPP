package com.ryl.engineer.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.warehouse.entity.InventoryCheck;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 盘库记录Mapper接口
 */
@Mapper
public interface InventoryCheckMapper extends BaseMapper<InventoryCheck> {

    /**
     * 获取仓库最近的盘库记录
     *
     * @param warehouseId 仓库ID
     * @param limit       记录数量
     * @return 盘库记录列表
     */
    @Select("SELECT * FROM inventory_check WHERE warehouse_id = #{warehouseId} ORDER BY check_time DESC LIMIT #{limit}")
    List<InventoryCheck> getRecentChecks(@Param("warehouseId") Long warehouseId, @Param("limit") Integer limit);

    /**
     * 获取未完成的盘库记录
     *
     * @return 未完成盘库记录列表
     */
    @Select("SELECT * FROM inventory_check WHERE status IN (0, 1) ORDER BY create_time DESC")
    List<InventoryCheck> getUnfinishedChecks();

    /**
     * 获取用户负责的盘库记录
     *
     * @param userId 用户ID
     * @return 盘库记录列表
     */
    @Select("SELECT * FROM inventory_check WHERE checker_id = #{userId} ORDER BY create_time DESC")
    List<InventoryCheck> getUserChecks(@Param("userId") Long userId);
} 