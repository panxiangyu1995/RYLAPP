package com.ryl.engineer.warehouse.dto.request;

import com.ryl.engineer.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库物品查询请求DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "仓库物品查询请求")
public class WarehouseItemQueryRequest extends PageRequest {

    @Schema(description = "仓库ID")
    private Long warehouseId;

    @Schema(description = "物品分类ID")
    private Long categoryId;

    @Schema(description = "物品名称(模糊查询)")
    private String name;

    @Schema(description = "物品编码(模糊查询)")
    private String code;

    @Schema(description = "是否只查询库存不足的物品，1=是，0=否")
    private Integer lowStock;
} 