package com.ryl.engineer.warehouse.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 物品入库请求DTO
 */
@Data
public class StockInRequest {

    /**
     * 物品ID
     */
    @NotNull(message = "物品ID不能为空")
    private Long itemId;

    /**
     * 入库数量
     */
    @NotNull(message = "入库数量不能为空")
    @Positive(message = "入库数量必须大于0")
    private Integer quantity;

    /**
     * 操作时间
     */
    @NotNull(message = "操作时间不能为空")
    private LocalDateTime operationTime;

    /**
     * 关联任务ID
     */
    private String taskId;

    /**
     * 描述/备注
     */
    @Size(max = 255, message = "描述/备注长度不能超过255个字符")
    private String description;
} 