package com.ryl.engineer.warehouse.dto;

import com.ryl.engineer.warehouse.entity.InventoryCheckDetail;
import lombok.Data;

/**
 * 盘库明细DTO
 */
@Data
public class InventoryCheckDetailDTO {

    /**
     * 明细ID
     */
    private Long id;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 物品编号
     */
    private String itemCode;

    /**
     * 物品名称
     */
    private String itemName;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 规格型号
     */
    private String model;

    /**
     * 系统数量
     */
    private Integer systemQuantity;

    /**
     * 实际数量
     */
    private Integer actualQuantity;

    /**
     * 差异数量
     */
    private Integer difference;

    /**
     * 备注
     */
    private String remark;

    /**
     * 从实体转换为DTO
     *
     * @param detail 盘库明细实体
     * @return InventoryCheckDetailDTO
     */
    public static InventoryCheckDetailDTO fromEntity(InventoryCheckDetail detail) {
        if (detail == null) {
            return null;
        }
        InventoryCheckDetailDTO dto = new InventoryCheckDetailDTO();
        dto.setId(detail.getId());
        dto.setItemId(detail.getItemId());
        dto.setSystemQuantity(detail.getSystemQuantity());
        dto.setActualQuantity(detail.getActualQuantity());
        dto.setDifference(detail.getDifference());
        dto.setRemark(detail.getRemark());
        return dto;
    }
} 