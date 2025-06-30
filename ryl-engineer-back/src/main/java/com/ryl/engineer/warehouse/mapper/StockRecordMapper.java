package com.ryl.engineer.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.warehouse.entity.StockRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 出入库记录Mapper接口
 */
@Mapper
public interface StockRecordMapper extends BaseMapper<StockRecord> {

    /**
     * 获取最近的出入库记录
     *
     * @param itemId 物品ID
     * @param limit  记录数量
     * @return 出入库记录列表
     */
    @Select("SELECT * FROM stock_record WHERE item_id = #{itemId} ORDER BY operation_time DESC LIMIT #{limit}")
    List<StockRecord> getRecentRecords(@Param("itemId") Long itemId, @Param("limit") Integer limit);

    /**
     * 获取指定前缀和年份的最大序号
     *
     * @param prefix 前缀（IN-入库，OUT-出库）
     * @param year   年份
     * @return 最大序号
     */
    @Select("SELECT MAX(CAST(SUBSTRING_INDEX(record_code, '-', -1) AS UNSIGNED)) " +
            "FROM stock_record " +
            "WHERE record_code LIKE CONCAT(#{prefix}, '-', #{year}, '-%')")
    Integer getMaxSequence(@Param("prefix") String prefix, @Param("year") String year);

    /**
     * 统计指定时间段内的入库数量和价值
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计结果
     */
    @Select("SELECT COUNT(*) as count, SUM(sr.quantity) as quantity, SUM(sr.quantity * wi.cost) as value " +
            "FROM stock_record sr " +
            "JOIN warehouse_item wi ON sr.item_id = wi.id " +
            "WHERE sr.record_type = 1 AND sr.operation_time BETWEEN #{startTime} AND #{endTime}")
    Map<String, Object> countInStats(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定时间段内的出库数量和价值
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计结果
     */
    @Select("SELECT COUNT(*) as count, SUM(sr.quantity) as quantity, SUM(sr.quantity * wi.cost) as value " +
            "FROM stock_record sr " +
            "JOIN warehouse_item wi ON sr.item_id = wi.id " +
            "WHERE sr.record_type = 2 AND sr.operation_time BETWEEN #{startTime} AND #{endTime}")
    Map<String, Object> countOutStats(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按分类统计指定时间段内的入库数量和价值
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计结果
     */
    @Select("SELECT wi.category_id, COUNT(*) as count, SUM(sr.quantity) as quantity, SUM(sr.quantity * wi.cost) as value " +
            "FROM stock_record sr " +
            "JOIN warehouse_item wi ON sr.item_id = wi.id " +
            "WHERE sr.record_type = 1 AND sr.operation_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY wi.category_id")
    List<Map<String, Object>> countInStatsByCategory(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按分类统计指定时间段内的出库数量和价值
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计结果
     */
    @Select("SELECT wi.category_id, COUNT(*) as count, SUM(sr.quantity) as quantity, SUM(sr.quantity * wi.cost) as value " +
            "FROM stock_record sr " +
            "JOIN warehouse_item wi ON sr.item_id = wi.id " +
            "WHERE sr.record_type = 2 AND sr.operation_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY wi.category_id")
    List<Map<String, Object>> countOutStatsByCategory(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
} 