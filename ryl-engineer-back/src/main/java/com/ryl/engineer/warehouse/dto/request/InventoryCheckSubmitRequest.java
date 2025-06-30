package com.ryl.engineer.warehouse.dto.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 盘库结果提交请求DTO
 */
@Data
public class InventoryCheckSubmitRequest {

    /**
     * 盘库ID
     */
    @NotNull(message = "盘库ID不能为空")
    private Long id;

    /**
     * 盘库明细
     */
    @NotEmpty(message = "盘库明细不能为空")
    @Valid
    private List<InventoryCheckDetailRequest> details;

    /**
     * 盘库明细请求DTO
     */
    @Data
    public static class InventoryCheckDetailRequest {
        /**
         * 物品ID
         */
        @NotNull(message = "物品ID不能为空")
        private Long itemId;

        /**
         * 实际数量
         */
        @NotNull(message = "实际数量不能为空")
        @PositiveOrZero(message = "实际数量必须大于等于0")
        private Integer actualQuantity;

        /**
         * 备注
         */
        @Size(max = 255, message = "备注长度不能超过255个字符")
        private String remark;
    }
} 