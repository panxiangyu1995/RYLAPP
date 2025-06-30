package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.common.dto.PageDTO;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.InventoryCheckDTO;
import com.ryl.engineer.warehouse.dto.InventoryCheckDetailDTO;
import com.ryl.engineer.warehouse.dto.request.InventoryCheckCreateRequest;
import com.ryl.engineer.warehouse.dto.request.InventoryCheckSubmitRequest;
import com.ryl.engineer.warehouse.service.InventoryCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 盘库管理控制器
 */
@RestController
@RequestMapping("/api/v1/warehouse/inventory-check")
public class InventoryCheckController {

    @Autowired
    private InventoryCheckService inventoryCheckService;

    /**
     * 获取盘库记录列表
     *
     * @param warehouseId 仓库ID
     * @param status      状态（0-待盘点，1-盘点中，2-已完成，null-全部）
     * @param pageNum     页码
     * @param pageSize    每页数量
     * @return 盘库记录列表
     */
    @GetMapping("/list")
    public Result<PageDTO<InventoryCheckDTO>> getCheckList(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ResponseDTO<PageDTO<InventoryCheckDTO>> responseDTO = inventoryCheckService.getCheckList(warehouseId, status, pageNum, pageSize);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 获取用户的盘库记录列表
     *
     * @param userId   用户ID
     * @param status   状态（0-待盘点，1-盘点中，2-已完成，null-全部）
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 盘库记录列表
     */
    @GetMapping("/user/{userId}")
    public Result<PageDTO<InventoryCheckDTO>> getUserCheckList(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ResponseDTO<PageDTO<InventoryCheckDTO>> responseDTO = inventoryCheckService.getUserCheckList(userId, status, pageNum, pageSize);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 获取盘库记录详情
     *
     * @param id 盘库记录ID
     * @return 盘库记录详情
     */
    @GetMapping("/{id}")
    public Result<InventoryCheckDTO> getCheckDetail(@PathVariable Long id) {
        ResponseDTO<InventoryCheckDTO> responseDTO = inventoryCheckService.getCheckDetail(id);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 获取盘库明细列表
     *
     * @param checkId  盘库记录ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 盘库明细列表
     */
    @GetMapping("/{checkId}/details")
    public Result<PageDTO<InventoryCheckDetailDTO>> getCheckDetailList(
            @PathVariable Long checkId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ResponseDTO<PageDTO<InventoryCheckDetailDTO>> responseDTO = inventoryCheckService.getCheckDetailList(checkId, pageNum, pageSize);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 获取盘库差异明细列表
     *
     * @param checkId  盘库记录ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 盘库差异明细列表
     */
    @GetMapping("/{checkId}/differences")
    public Result<PageDTO<InventoryCheckDetailDTO>> getDifferenceDetailList(
            @PathVariable Long checkId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ResponseDTO<PageDTO<InventoryCheckDetailDTO>> responseDTO = inventoryCheckService.getDifferenceDetailList(checkId, pageNum, pageSize);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 创建盘库任务
     *
     * @param request 创建请求
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<Long> createInventoryCheck(@RequestBody @Validated InventoryCheckCreateRequest request) {
        ResponseDTO<Long> responseDTO = inventoryCheckService.createInventoryCheck(request);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 开始盘库
     *
     * @param id 盘库记录ID
     * @return 开始结果
     */
    @PostMapping("/{id}/start")
    public Result<Void> startInventoryCheck(@PathVariable Long id) {
        ResponseDTO<Void> responseDTO = inventoryCheckService.startInventoryCheck(id);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 提交盘库结果
     *
     * @param request 提交请求
     * @return 提交结果
     */
    @PostMapping("/submit")
    public Result<Void> submitInventoryCheck(@RequestBody @Validated InventoryCheckSubmitRequest request) {
        ResponseDTO<Void> responseDTO = inventoryCheckService.submitInventoryCheck(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 完成盘库
     *
     * @param id     盘库记录ID
     * @param remark 备注
     * @return 完成结果
     */
    @PostMapping("/{id}/complete")
    public Result<Void> completeInventoryCheck(
            @PathVariable Long id,
            @RequestParam(required = false) String remark) {
        ResponseDTO<Void> responseDTO = inventoryCheckService.completeInventoryCheck(id, remark);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }
} 