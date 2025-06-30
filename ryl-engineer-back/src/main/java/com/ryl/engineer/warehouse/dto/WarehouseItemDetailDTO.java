package com.ryl.engineer.warehouse.dto;

import com.ryl.engineer.warehouse.entity.WarehouseItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 物品详情DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseItemDetailDTO {

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
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 详细信息（根据分类不同而不同）
     */
    private Map<String, Object> details;

    /**
     * 最近的出入库记录
     */
    private List<StockRecordDTO> stockRecords;

    /**
     * 从实体转换为DTO
     *
     * @param item 物品实体
     * @return WarehouseItemDetailDTO
     */
    public static WarehouseItemDetailDTO fromEntity(WarehouseItem item) {
        if (item == null) {
            return null;
        }
        WarehouseItemDetailDTO dto = new WarehouseItemDetailDTO();
        dto.setId(item.getId());
        dto.setItemCode(item.getItemCode());
        dto.setCategoryId(item.getCategoryId());
        dto.setWarehouseId(item.getWarehouseId());
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
        return dto;
    }
} 