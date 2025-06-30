package com.ryl.engineer.warehouse.dto;

import com.ryl.engineer.warehouse.entity.Warehouse;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 仓库DTO
 */
@Data
public class WarehouseDTO {

    /**
     * 仓库ID
     */
    private Long id;

    /**
     * 仓库编码
     */
    private String code;

    /**
     * 仓库名称
     */
    private String name;

    /**
     * 仓库地点
     */
    private String location;

    /**
     * 仓库描述
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
     * 从实体转换为DTO
     *
     * @param warehouse 仓库实体
     * @return WarehouseDTO
     */
    public static WarehouseDTO fromEntity(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(warehouse.getId());
        dto.setCode(warehouse.getCode());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        dto.setDescription(warehouse.getDescription());
        dto.setStatus(warehouse.getStatus());
        dto.setCreateTime(warehouse.getCreateTime());
        dto.setUpdateTime(warehouse.getUpdateTime());
        return dto;
    }
} 