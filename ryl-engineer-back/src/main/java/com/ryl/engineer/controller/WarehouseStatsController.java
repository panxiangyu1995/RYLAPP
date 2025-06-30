package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.StockStatsDTO;
import com.ryl.engineer.warehouse.service.StockRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 仓库统计控制器
 */
@RestController
@RequestMapping("/api/v1/warehouse/stats")
public class WarehouseStatsController {

    @Autowired
    private StockRecordService stockRecordService;

    /**
     * 获取出入库统计
     *
     * @param warehouseId 仓库ID
     * @param startDate   开始日期
     * @param endDate     结束日期
     * @return 统计结果
     */
    @GetMapping("/stock")
    public Result<StockStatsDTO> getStockStats(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ResponseDTO<StockStatsDTO> responseDTO = stockRecordService.getStockStats(warehouseId, startDate, endDate);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 获取库存价值统计
     *
     * @param warehouseId 仓库ID
     * @return 统计结果
     */
    @GetMapping("/value")
    public Result<Object> getInventoryValue(@RequestParam(required = false) Long warehouseId) {
        // 这里应该调用相应的服务方法，但目前没有实现，返回一个空的成功结果
        return Result.success();
    }

    /**
     * 获取库存分类统计
     *
     * @param warehouseId 仓库ID
     * @return 统计结果
     */
    @GetMapping("/category")
    public Result<Object> getCategoryStats(@RequestParam(required = false) Long warehouseId) {
        // 这里应该调用相应的服务方法，但目前没有实现，返回一个空的成功结果
        return Result.success();
    }
} 