package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.common.dto.PageDTO;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.ItemRequestDTO;
import com.ryl.engineer.warehouse.dto.ItemUsageStatsDTO;
import com.ryl.engineer.warehouse.dto.request.RequestProcessRequest;
import com.ryl.engineer.warehouse.service.ItemRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 物品申请管理控制器
 */
@RestController
@RequestMapping("/api/v1/warehouse/request")
public class ItemRequestController {

    @Autowired
    private ItemRequestService itemRequestService;

    /**
     * 获取申请列表
     *
     * @param requestType 申请类型（1-使用申请，2-采购申请，null-全部）
     * @param status      状态（0-待审批，1-已批准，2-已拒绝，null-全部）
     * @param pageNum     页码
     * @param pageSize    每页数量
     * @return 申请列表
     */
    @GetMapping("/list")
    public Result<PageDTO<ItemRequestDTO>> getRequestList(
            @RequestParam(required = false) Integer requestType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ResponseDTO<PageDTO<ItemRequestDTO>> responseDTO = itemRequestService.getRequestList(requestType, status, pageNum, pageSize);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

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
    @GetMapping("/user/{userId}")
    public Result<PageDTO<ItemRequestDTO>> getUserRequestList(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer requestType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ResponseDTO<PageDTO<ItemRequestDTO>> responseDTO = itemRequestService.getUserRequestList(userId, requestType, status, pageNum, pageSize);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 获取任务相关的申请列表
     *
     * @param taskId   任务ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 申请列表
     */
    @GetMapping("/task/{taskId}")
    public Result<PageDTO<ItemRequestDTO>> getTaskRequestList(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ResponseDTO<PageDTO<ItemRequestDTO>> responseDTO = itemRequestService.getTaskRequestList(taskId, pageNum, pageSize);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 获取申请详情
     *
     * @param id 申请ID
     * @return 申请详情
     */
    @GetMapping("/{id}")
    public Result<ItemRequestDTO> getRequestDetail(@PathVariable Long id) {
        ResponseDTO<ItemRequestDTO> responseDTO = itemRequestService.getRequestDetail(id);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 处理申请
     *
     * @param request 处理请求
     * @return 处理结果
     */
    @PostMapping("/process")
    public Result<Void> processRequest(@RequestBody @Validated RequestProcessRequest request) {
        ResponseDTO<Void> responseDTO = itemRequestService.processRequest(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 获取物品使用统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 使用统计
     */
    @GetMapping("/usage-stats")
    public Result<ItemUsageStatsDTO> getItemUsageStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ResponseDTO<ItemUsageStatsDTO> responseDTO = itemRequestService.getItemUsageStats(startDate, endDate);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }
} 