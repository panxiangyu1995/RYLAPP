package com.ryl.engineer.warehouse.dto;

import lombok.Data;

/**
 * 二维码解析结果DTO
 */
@Data
public class QRCodeResultDTO {

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 物品编号
     */
    private String itemCode;

    /**
     * 物品详情
     */
    private WarehouseItemDTO item;
} 