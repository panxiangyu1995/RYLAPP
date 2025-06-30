package com.ryl.engineer.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物品申请实体类
 */
@Data
@TableName("item_request")
public class ItemRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 物品名称（采购申请可能是新物品）
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
     * 更新时间
     */
    private LocalDateTime updateTime;
} 