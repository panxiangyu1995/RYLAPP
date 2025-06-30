package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.common.dto.PageDTO;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.WarehouseDTO;
import com.ryl.engineer.warehouse.dto.WarehouseDetailDTO;
import com.ryl.engineer.warehouse.dto.request.DeleteRequest;
import com.ryl.engineer.warehouse.dto.request.WarehouseAddRequest;
import com.ryl.engineer.warehouse.dto.request.WarehouseUpdateRequest;
import com.ryl.engineer.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仓库管理控制器
 */
@RestController
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    /**
     * 获取所有仓库列表
     *
     * @return 仓库列表
     */
    @GetMapping("/list")
    public Result<List<WarehouseDTO>> getWarehouseList() {
        ResponseDTO<List<WarehouseDTO>> responseDTO = warehouseService.getAllWarehouses();
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 分页获取仓库列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页仓库列表
     */
    @GetMapping("/page")
    public Result<PageDTO<WarehouseDTO>> getWarehousePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ResponseDTO<PageDTO<WarehouseDTO>> responseDTO = warehouseService.getWarehouseList(pageNum, pageSize);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 获取仓库详情
     *
     * @param id 仓库ID
     * @return 仓库详情
     */
    @GetMapping("/{id}")
    public Result<WarehouseDetailDTO> getWarehouseDetail(@PathVariable Long id) {
        ResponseDTO<WarehouseDetailDTO> responseDTO = warehouseService.getWarehouseDetail(id);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 添加仓库
     *
     * @param request 添加请求
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result<Void> addWarehouse(@RequestBody @Validated WarehouseAddRequest request) {
        ResponseDTO<Void> responseDTO = warehouseService.addWarehouse(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 更新仓库
     *
     * @param request 更新请求
     * @return 更新结果
     */
    @PutMapping("/update")
    public Result<Void> updateWarehouse(@RequestBody @Validated WarehouseUpdateRequest request) {
        ResponseDTO<Void> responseDTO = warehouseService.updateWarehouse(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 删除仓库
     *
     * @param request 删除请求
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteWarehouse(@RequestBody @Validated DeleteRequest request) {
        ResponseDTO<Void> responseDTO = warehouseService.deleteWarehouse(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }
} 