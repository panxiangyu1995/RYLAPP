package com.ryl.engineer.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.warehouse.dto.InventoryStatsDTO;
import com.ryl.engineer.warehouse.entity.WarehouseItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 物品Mapper接口
 */
@Mapper
public interface WarehouseItemMapper extends BaseMapper<WarehouseItem> {

    /**
     * 增加物品数量
     *
     * @param id       物品ID
     * @param quantity 增加数量
     * @return 影响行数
     */
    @Update("UPDATE warehouse_item SET quantity = quantity + #{quantity} WHERE id = #{id}")
    int increaseQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 减少物品数量
     *
     * @param id       物品ID
     * @param quantity 减少数量
     * @return 影响行数
     */
    @Update("UPDATE warehouse_item SET quantity = quantity - #{quantity} WHERE id = #{id} AND quantity >= #{quantity}")
    int decreaseQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 统计仓库中物品总数
     *
     * @param warehouseId 仓库ID
     * @return 物品总数
     */
    @Select("SELECT COUNT(*) FROM warehouse_item WHERE warehouse_id = #{warehouseId}")
    int countByWarehouseId(@Param("warehouseId") Long warehouseId);

    /**
     * 统计仓库中指定分类的物品数量
     *
     * @param warehouseId 仓库ID
     * @param categoryId  分类ID
     * @return 物品数量
     */
    @Select("SELECT COUNT(*) FROM warehouse_item WHERE warehouse_id = #{warehouseId} AND category_id = #{categoryId}")
    int countByWarehouseIdAndCategoryId(@Param("warehouseId") Long warehouseId, @Param("categoryId") Long categoryId);

    /**
     * 获取下一个序列号
     *
     * @param categoryId 分类ID
     * @return 序列号
     */
    @Select("SELECT IFNULL(MAX(CAST(SUBSTRING_INDEX(item_code, '-', -1) AS UNSIGNED)), 0) + 1 " +
            "FROM warehouse_item " +
            "WHERE category_id = #{categoryId} " +
            "AND item_code REGEXP '^[A-Z]+-[0-9]+-[0-9]+$'")
    Integer getNextSequence(@Param("categoryId") Long categoryId);

    /**
     * 计算所有物品的总价值
     *
     * @return 总价值
     */
    @Select("SELECT IFNULL(SUM(quantity * cost), 0) FROM warehouse_item")
    BigDecimal calculateTotalValue();

    /**
     * 获取分类统计
     *
     * @return 分类统计列表
     */
    @Select("SELECT " +
            "wi.category_id AS categoryId, " +
            "ic.name AS categoryName, " +
            "COUNT(wi.id) AS itemCount, " +
            "IFNULL(SUM(wi.quantity * wi.cost), 0) AS totalValue " +
            "FROM warehouse_item wi " +
            "LEFT JOIN item_category ic ON wi.category_id = ic.id " +
            "GROUP BY wi.category_id, ic.name")
    List<InventoryStatsDTO.CategoryStatsDTO> getCategoryStats();

    /**
     * 获取仓库统计
     *
     * @return 仓库统计列表
     */
    @Select("SELECT " +
            "wi.warehouse_id AS warehouseId, " +
            "w.name AS warehouseName, " +
            "COUNT(wi.id) AS itemCount, " +
            "IFNULL(SUM(wi.quantity * wi.cost), 0) AS totalValue " +
            "FROM warehouse_item wi " +
            "LEFT JOIN warehouse w ON wi.warehouse_id = w.id " +
            "GROUP BY wi.warehouse_id, w.name")
    List<InventoryStatsDTO.WarehouseStatsDTO> getWarehouseStats();
} 