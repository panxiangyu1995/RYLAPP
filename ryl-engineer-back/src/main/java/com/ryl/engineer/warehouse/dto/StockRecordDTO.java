package com.ryl.engineer.warehouse.dto;

import com.ryl.engineer.warehouse.entity.StockRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 出入库记录DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRecordDTO {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 记录编号
     */
    private String recordCode;

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
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 记录类型（1-入库，2-出库）
     */
    private Integer recordType;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

    /**
     * 关联任务ID
     */
    private String taskId;

    /**
     * 描述/备注
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 从实体转换为DTO
     *
     * @param record 出入库记录实体
     * @return StockRecordDTO
     */
    public static StockRecordDTO fromEntity(StockRecord record) {
        if (record == null) {
            return null;
        }
        StockRecordDTO dto = new StockRecordDTO();
        dto.setId(record.getId());
        dto.setRecordCode(record.getRecordCode());
        dto.setItemId(record.getItemId());
        dto.setRecordType(record.getRecordType());
        dto.setQuantity(record.getQuantity());
        dto.setOperatorId(record.getOperatorId());
        dto.setOperatorName(record.getOperatorName());
        dto.setOperationTime(record.getOperationTime());
        dto.setTaskId(record.getTaskId());
        dto.setDescription(record.getDescription());
        dto.setCreateTime(record.getCreateTime());
        return dto;
    }
} 