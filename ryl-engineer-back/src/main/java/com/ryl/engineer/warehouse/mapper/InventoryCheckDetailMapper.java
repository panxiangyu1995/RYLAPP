package com.ryl.engineer.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.warehouse.entity.InventoryCheckDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 盘库明细Mapper接口
 */
@Mapper
public interface InventoryCheckDetailMapper extends BaseMapper<InventoryCheckDetail> {

    /**
     * 根据盘库ID统计明细数量
     *
     * @param checkId 盘库ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM inventory_check_detail WHERE check_id = #{checkId}")
    Integer countByCheckId(@Param("checkId") Long checkId);

    /**
     * 根据盘库ID统计差异明细数量
     *
     * @param checkId 盘库ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM inventory_check_detail WHERE check_id = #{checkId} AND actual_quantity != system_quantity")
    Integer countDifferenceByCheckId(@Param("checkId") Long checkId);

    /**
     * 获取盘库记录的明细
     *
     * @param checkId 盘库记录ID
     * @return 盘库明细列表
     */
    @Select("SELECT * FROM inventory_check_detail WHERE check_id = #{checkId}")
    List<InventoryCheckDetail> getByCheckId(@Param("checkId") Long checkId);

    /**
     * 获取盘库记录的差异明细
     *
     * @param checkId 盘库记录ID
     * @return 差异明细列表
     */
    @Select("SELECT * FROM inventory_check_detail WHERE check_id = #{checkId} AND actual_quantity != system_quantity")
    List<InventoryCheckDetail> getDifferenceDetails(@Param("checkId") Long checkId);

    /**
     * 统计盘库差异情况
     *
     * @param checkId 盘库记录ID
     * @return 统计结果
     */
    @Select("SELECT " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN actual_quantity = system_quantity THEN 1 ELSE 0 END) as match_count, " +
            "SUM(CASE WHEN actual_quantity > system_quantity THEN 1 ELSE 0 END) as over_count, " +
            "SUM(CASE WHEN actual_quantity < system_quantity THEN 1 ELSE 0 END) as under_count, " +
            "SUM(CASE WHEN actual_quantity > system_quantity THEN actual_quantity - system_quantity ELSE 0 END) as over_quantity, " +
            "SUM(CASE WHEN actual_quantity < system_quantity THEN system_quantity - actual_quantity ELSE 0 END) as under_quantity " +
            "FROM inventory_check_detail WHERE check_id = #{checkId}")
    Map<String, Object> countDifferenceStats(@Param("checkId") Long checkId);
} 