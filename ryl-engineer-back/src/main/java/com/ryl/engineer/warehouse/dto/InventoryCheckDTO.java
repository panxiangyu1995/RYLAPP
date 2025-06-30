package com.ryl.engineer.warehouse.dto;

import com.ryl.engineer.warehouse.entity.InventoryCheck;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 盘库记录DTO
 */
@Data
public class InventoryCheckDTO {

    /**
     * 盘库ID
     */
    private Long id;

    /**
     * 盘库编号
     */
    private String checkCode;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 盘库人ID
     */
    private Long checkerId;

    /**
     * 盘库人姓名
     */
    private String checkerName;

    /**
     * 盘库时间
     */
    private LocalDateTime checkTime;

    /**
     * 状态（0-进行中，1-已完成）
     */
    private Integer status;

    /**
     * 描述/备注
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 盘点物品数量
     */
    private Integer itemCount;

    /**
     * 差异物品数量
     */
    private Integer differenceCount;

    /**
     * 盘库明细
     */
    private List<InventoryCheckDetailDTO> details;

    /**
     * 从实体转换为DTO
     *
     * @param check 盘库记录实体
     * @return InventoryCheckDTO
     */
    public static InventoryCheckDTO fromEntity(InventoryCheck check) {
        if (check == null) {
            return null;
        }
        InventoryCheckDTO dto = new InventoryCheckDTO();
        dto.setId(check.getId());
        dto.setCheckCode(check.getCheckCode());
        dto.setWarehouseId(check.getWarehouseId());
        dto.setCheckerId(check.getCheckerId());
        dto.setCheckerName(check.getCheckerName());
        dto.setCheckTime(check.getCheckTime());
        dto.setStatus(check.getStatus());
        dto.setDescription(check.getDescription());
        dto.setCreateTime(check.getCreateTime());
        dto.setUpdateTime(check.getUpdateTime());
        return dto;
    }
} 