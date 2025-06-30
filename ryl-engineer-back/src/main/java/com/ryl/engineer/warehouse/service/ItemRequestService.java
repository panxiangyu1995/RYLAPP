package com.ryl.engineer.warehouse.service;

import com.ryl.engineer.common.dto.PageDTO;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.ItemRequestDTO;
import com.ryl.engineer.warehouse.dto.ItemUsageStatsDTO;
import com.ryl.engineer.warehouse.dto.request.RequestProcessRequest;

import java.time.LocalDate;

/**
 * 物品申请服务接口
 */
public interface ItemRequestService {

    /**
     * 获取申请列表
     *
     * @param requestType 申请类型（1-使用申请，2-采购申请，null-全部）
     * @param status      状态（0-待审批，1-已批准，2-已拒绝，null-全部）
     * @param pageNum     页码
     * @param pageSize    每页数量
     * @return 申请列表
     */
    ResponseDTO<PageDTO<ItemRequestDTO>> getRequestList(Integer requestType, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取用户的申请列表
     *
     * @param userId      用户ID
     * @param requestType 申请类型（1-使用申请，2-采购申请，null-全部）
     * @param status      状态（0-待审批，1-已批准，2-已拒绝，null-全部）
     * @param pageNum     页码
     * @param pageSize    每页数量
     * @return 申请列表
     */
    ResponseDTO<PageDTO<ItemRequestDTO>> getUserRequestList(Long userId, Integer requestType, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取任务相关的申请列表
     *
     * @param taskId   任务ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 申请列表
     */
    ResponseDTO<PageDTO<ItemRequestDTO>> getTaskRequestList(String taskId, Integer pageNum, Integer pageSize);

    /**
     * 获取申请详情
     *
     * @param requestId 申请ID
     * @return 申请详情
     */
    ResponseDTO<ItemRequestDTO> getRequestDetail(Long requestId);

    /**
     * 处理申请
     *
     * @param request 处理请求
     * @return 处理结果
     */
    ResponseDTO<Void> processRequest(RequestProcessRequest request);

    /**
     * 获取物品使用统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 使用统计
     */
    ResponseDTO<ItemUsageStatsDTO> getItemUsageStats(LocalDate startDate, LocalDate endDate);
} 