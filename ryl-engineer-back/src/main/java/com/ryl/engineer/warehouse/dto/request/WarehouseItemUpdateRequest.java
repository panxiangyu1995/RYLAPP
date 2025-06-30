package com.ryl.engineer.warehouse.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 更新物品请求DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarehouseItemUpdateRequest extends WarehouseItemAddRequest {

    /**
     * 物品ID
     */
    @NotNull(message = "物品ID不能为空")
    private Long id;
} 