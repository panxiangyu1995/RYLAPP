package com.ryl.engineer.warehouse.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 物品使用统计DTO
 */
@Data
public class ItemUsageStatsDTO {

    /**
     * 使用量最多的物品
     */
    private List<TopItemDTO> topItems;

    /**
     * 按任务统计
     */
    private List<TaskStatsDTO> taskStats;

    /**
     * 按用户统计
     */
    private List<UserStatsDTO> userStats;

    /**
     * 使用量最多的物品DTO
     */
    @Data
    public static class TopItemDTO {
        /**
         * 物品ID
         */
        private Long itemId;

        /**
         * 物品名称
         */
        private String itemName;

        /**
         * 使用数量
         */
        private Integer quantity;

        /**
         * 使用占比（百分比）
         */
        private BigDecimal percentage;
    }

    /**
     * 按任务统计DTO
     */
    @Data
    public static class TaskStatsDTO {
        /**
         * 任务ID
         */
        private String taskId;

        /**
         * 任务名称
         */
        private String taskName;

        /**
         * 使用数量
         */
        private Integer quantity;

        /**
         * 使用价值
         */
        private BigDecimal value;
    }

    /**
     * 按用户统计DTO
     */
    @Data
    public static class UserStatsDTO {
        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 用户姓名
         */
        private String userName;

        /**
         * 使用数量
         */
        private Integer quantity;

        /**
         * 使用价值
         */
        private BigDecimal value;
    }
} 