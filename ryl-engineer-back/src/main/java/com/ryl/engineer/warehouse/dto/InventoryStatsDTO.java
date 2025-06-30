package com.ryl.engineer.warehouse.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存统计DTO
 */
@Data
public class InventoryStatsDTO {

    /**
     * 物品总数
     */
    private Integer totalItems;

    /**
     * 总价值
     */
    private BigDecimal totalValue;

    /**
     * 分类统计
     */
    private List<CategoryStatsDTO> categoryStats;

    /**
     * 仓库统计
     */
    private List<WarehouseStatsDTO> warehouseStats;

    /**
     * 分类统计DTO
     */
    @Data
    public static class CategoryStatsDTO {
        /**
         * 分类ID
         */
        private Long categoryId;

        /**
         * 分类名称
         */
        private String categoryName;

        /**
         * 物品数量
         */
        private Integer itemCount;

        /**
         * 总价值
         */
        private BigDecimal totalValue;
    }

    /**
     * 仓库统计DTO
     */
    @Data
    public static class WarehouseStatsDTO {
        /**
         * 仓库ID
         */
        private Long warehouseId;

        /**
         * 仓库名称
         */
        private String warehouseName;

        /**
         * 物品数量
         */
        private Integer itemCount;

        /**
         * 总价值
         */
        private BigDecimal totalValue;
    }
}