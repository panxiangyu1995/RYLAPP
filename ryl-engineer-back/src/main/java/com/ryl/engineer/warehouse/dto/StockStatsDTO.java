package com.ryl.engineer.warehouse.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 出入库统计DTO
 */
@Data
public class StockStatsDTO {

    /**
     * 入库统计
     */
    private List<StockDateStatsDTO> inStats;

    /**
     * 出库统计
     */
    private List<StockDateStatsDTO> outStats;

    /**
     * 分类统计
     */
    private List<CategoryStatsDTO> categoryStats;

    /**
     * 按日期统计DTO
     */
    @Data
    public static class StockDateStatsDTO {
        /**
         * 日期（根据groupBy格式不同）
         */
        private String date;

        /**
         * 次数
         */
        private Integer count;

        /**
         * 总数量
         */
        private Integer quantity;

        /**
         * 总价值
         */
        private BigDecimal value;
    }

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
         * 入库次数
         */
        private Integer inCount;

        /**
         * 入库总数量
         */
        private Integer inQuantity;

        /**
         * 入库总价值
         */
        private BigDecimal inValue;

        /**
         * 出库次数
         */
        private Integer outCount;

        /**
         * 出库总数量
         */
        private Integer outQuantity;

        /**
         * 出库总价值
         */
        private BigDecimal outValue;
    }
} 