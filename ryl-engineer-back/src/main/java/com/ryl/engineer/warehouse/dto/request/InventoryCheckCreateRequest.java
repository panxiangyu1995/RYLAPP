package com.ryl.engineer.warehouse.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 盘库任务请求DTO
 */
@Data
public class InventoryCheckCreateRequest {

    /**
     * 仓库ID
     */
    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    /**
     * 盘库时间
     */
    @NotNull(message = "盘库时间不能为空")
    private LocalDateTime checkTime;

    /**
     * 描述/备注
     */
    @Size(max = 255, message = "描述/备注长度不能超过255个字符")
    private String description;
} 