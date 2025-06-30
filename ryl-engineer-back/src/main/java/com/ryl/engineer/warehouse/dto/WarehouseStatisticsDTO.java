package com.ryl.engineer.warehouse.dto;

import lombok.Data;

/**
 * 仓库统计DTO
 */
@Data
public class WarehouseStatisticsDTO {

    /**
     * 物品总数
     */
    private Integer totalItems;

    /**
     * 仪器数量
     */
    private Integer instrumentCount;

    /**
     * 配件数量
     */
    private Integer partCount;

    /**
     * 耗材数量
     */
    private Integer consumableCount;

    /**
     * 固定资产数量
     */
    private Integer assetCount;
} 