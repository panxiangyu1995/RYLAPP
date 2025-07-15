package com.ryl.engineer.warehouse.service;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.InventoryCheckDTO;
import com.ryl.engineer.warehouse.dto.InventoryCheckDetailDTO;
import com.ryl.engineer.warehouse.dto.request.InventoryCheckCreateRequest;
import com.ryl.engineer.warehouse.dto.request.InventoryCheckSubmitRequest;

import java.util.List;

/**
 * 盘库服务接口
 */
public interface InventoryCheckService {

    /**
     * 获取盘库记录列表
     *
     * @param warehouseId 仓库ID
     * @param status      状态（0-待盘点，1-盘点中，2-已完成，null-全部）
     * @param pageNum     页码
     * @param pageSize    每页数量
     * @return 盘库记录列表
     */
    PageResult<InventoryCheckDTO> getCheckList(Long warehouseId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取用户的盘库记录列表
     *
     * @param userId   用户ID
     * @param status   状态（0-待盘点，1-盘点中，2-已完成，null-全部）
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 盘库记录列表
     */
    PageResult<InventoryCheckDTO> getUserCheckList(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取盘库记录详情
     *
     * @param checkId 盘库记录ID
     * @return 盘库记录详情
     */
    ResponseDTO<InventoryCheckDTO> getCheckDetail(Long checkId);

    /**
     * 获取盘库明细列表
     *
     * @param checkId  盘库记录ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 盘库明细列表
     */
    PageResult<InventoryCheckDetailDTO> getCheckDetailList(Long checkId, Integer pageNum, Integer pageSize);

    /**
     * 获取盘库差异明细列表
     *
     * @param checkId  盘库记录ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 盘库差异明细列表
     */
    PageResult<InventoryCheckDetailDTO> getDifferenceDetailList(Long checkId, Integer pageNum, Integer pageSize);

    /**
     * 创建盘库任务
     *
     * @param request 创建请求
     * @return 创建结果
     */
    ResponseDTO<Long> createInventoryCheck(InventoryCheckCreateRequest request);

    /**
     * 开始盘库
     *
     * @param checkId 盘库记录ID
     * @return 开始结果
     */
    ResponseDTO<Void> startInventoryCheck(Long checkId);

    /**
     * 提交盘库结果
     *
     * @param request 提交请求
     * @return 提交结果
     */
    ResponseDTO<Void> submitInventoryCheck(InventoryCheckSubmitRequest request);

    /**
     * 完成盘库
     *
     * @param checkId 盘库记录ID
     * @param remark  备注
     * @return 完成结果
     */
    ResponseDTO<Void> completeInventoryCheck(Long checkId, String remark);
} 