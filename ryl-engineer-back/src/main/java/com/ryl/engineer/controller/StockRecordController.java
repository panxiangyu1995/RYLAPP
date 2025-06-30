package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.common.dto.PageDTO;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.StockRecordDTO;
import com.ryl.engineer.warehouse.dto.request.StockInRequest;
import com.ryl.engineer.warehouse.dto.request.StockOutRequest;
import com.ryl.engineer.warehouse.service.StockRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 出入库管理控制器
 */
@RestController
@RequestMapping("/api/v1/warehouse/stock")
public class StockRecordController {

    @Autowired
    private StockRecordService stockRecordService;

    /**
     * 物品入库
     *
     * @param request 入库请求
     * @return 入库结果
     */
    @PostMapping("/in")
    public Result<Void> stockIn(@RequestBody @Validated StockInRequest request) {
        ResponseDTO<Void> responseDTO = stockRecordService.stockIn(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 物品出库
     *
     * @param request 出库请求
     * @return 出库结果
     */
    @PostMapping("/out")
    public Result<Void> stockOut(@RequestBody @Validated StockOutRequest request) {
        ResponseDTO<Void> responseDTO = stockRecordService.stockOut(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 获取出入库记录
     *
     * @param itemId     物品ID
     * @param recordType 记录类型（1-入库，2-出库）
     * @param warehouseId 仓库ID
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 出入库记录列表
     */
    @GetMapping("/records")
    public Result<PageDTO<StockRecordDTO>> getRecords(
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Integer recordType,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        ResponseDTO<PageDTO<StockRecordDTO>> responseDTO = stockRecordService.getRecords(itemId, recordType, warehouseId, startTime, endTime, pageNum, pageSize);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }
} 