package com.ryl.engineer.warehouse.dto;

import com.ryl.engineer.warehouse.entity.WarehouseItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 物品DTO
 */
@Data
public class WarehouseItemDTO {

    /**
     * 物品ID
     */
    private Long id;

    /**
     * 物品编号
     */
    private String itemCode;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 物品名称
     */
    private String name;

    /**
     * 规格型号
     */
    private String model;

    /**
     * 厂家/品牌
     */
    private String manufacturer;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 成本
     */
    private BigDecimal cost;

    /**
     * 到货日期
     */
    private LocalDate arrivalDate;

    /**
     * 区域
     */
    private String area;

    /**
     * 描述/备注
     */
    private String description;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 库存状态（库存充足/库存紧张/缺货）
     */
    private String stockStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 从实体转换为DTO
     *
     * @param item 物品实体
     * @return WarehouseItemDTO
     */
    public static WarehouseItemDTO fromEntity(WarehouseItem item) {
        if (item == null) {
            return null;
        }
        WarehouseItemDTO dto = new WarehouseItemDTO();
        dto.setId(item.getId());
        dto.setItemCode(item.getItemCode());
        dto.setCategoryId(item.getCategoryId());
        dto.setName(item.getName());
        dto.setModel(item.getModel());
        dto.setManufacturer(item.getManufacturer());
        dto.setQuantity(item.getQuantity());
        dto.setCost(item.getCost());
        dto.setArrivalDate(item.getArrivalDate());
        dto.setArea(item.getArea());
        dto.setDescription(item.getDescription());
        dto.setStatus(item.getStatus());
        dto.setCreateTime(item.getCreateTime());
        dto.setUpdateTime(item.getUpdateTime());
        
        // 设置库存状态
        if (item.getQuantity() <= 0) {
            dto.setStockStatus("缺货");
        } else if (item.getQuantity() <= 5) {
            dto.setStockStatus("库存紧张");
        } else {
            dto.setStockStatus("库存充足");
        }
        
        return dto;
    }
} 