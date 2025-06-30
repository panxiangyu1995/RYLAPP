package com.ryl.engineer.warehouse.dto;

import com.ryl.engineer.warehouse.entity.ItemRequest;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物品申请DTO
 */
@Data
public class ItemRequestDTO {

    /**
     * 申请ID
     */
    private Long id;

    /**
     * 申请编号
     */
    private String requestCode;

    /**
     * 申请人ID
     */
    private Long requesterId;

    /**
     * 申请人姓名
     */
    private String requesterName;

    /**
     * 申请类型（1-使用申请，2-采购申请）
     */
    private Integer requestType;

    /**
     * 状态（0-待审批，1-已同意，2-已拒绝）
     */
    private Integer status;

    /**
     * 物品ID（使用申请）
     */
    private Long itemId;

    /**
     * 物品名称
     */
    private String itemName;

    /**
     * 规格型号
     */
    private String itemModel;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 关联任务ID
     */
    private String taskId;

    /**
     * 用途
     */
    private String purpose;

    /**
     * 紧急程度（1-普通，2-紧急，3-特急）
     */
    private Integer urgency;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批人姓名
     */
    private String approverName;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    /**
     * 审批意见
     */
    private String approvalComment;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 从实体转换为DTO
     *
     * @param request 物品申请实体
     * @return ItemRequestDTO
     */
    public static ItemRequestDTO fromEntity(ItemRequest request) {
        if (request == null) {
            return null;
        }
        ItemRequestDTO dto = new ItemRequestDTO();
        dto.setId(request.getId());
        dto.setRequestCode(request.getRequestCode());
        dto.setRequesterId(request.getRequesterId());
        dto.setRequesterName(request.getRequesterName());
        dto.setRequestType(request.getRequestType());
        dto.setStatus(request.getStatus());
        dto.setItemId(request.getItemId());
        dto.setItemName(request.getItemName());
        dto.setItemModel(request.getItemModel());
        dto.setQuantity(request.getQuantity());
        dto.setTaskId(request.getTaskId());
        dto.setPurpose(request.getPurpose());
        dto.setUrgency(request.getUrgency());
        dto.setApproverId(request.getApproverId());
        dto.setApproverName(request.getApproverName());
        dto.setApprovalTime(request.getApprovalTime());
        dto.setApprovalComment(request.getApprovalComment());
        dto.setCreateTime(request.getCreateTime());
        return dto;
    }
} 