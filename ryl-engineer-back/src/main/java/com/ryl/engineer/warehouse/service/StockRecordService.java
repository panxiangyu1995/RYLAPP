package com.ryl.engineer.warehouse.service;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.StockRecordDTO;
import com.ryl.engineer.warehouse.dto.StockStatsDTO;
import com.ryl.engineer.warehouse.dto.request.StockInRequest;
import com.ryl.engineer.warehouse.dto.request.StockOutRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 出入库记录服务接口
 */
public interface StockRecordService {

    /**
     * 物品入库
     *
     * @param request 入库请求
     * @return 操作结果
     */
    ResponseDTO<Void> stockIn(StockInRequest request);

    /**
     * 物品出库
     *
     * @param request 出库请求
     * @return 操作结果
     */
    ResponseDTO<Void> stockOut(StockOutRequest request);

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
    PageResult<StockRecordDTO> getRecords(Long itemId, Integer recordType, Long warehouseId, 
                                                  LocalDateTime startTime, LocalDateTime endTime, 
                                                  Integer pageNum, Integer pageSize);

    /**
     * 获取物品的出入库记录
     *
     * @param itemId    物品ID
     * @param recordType 记录类型（1-入库，2-出库，null-全部）
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @return 出入库记录列表
     */
    PageResult<StockRecordDTO> getItemRecords(Long itemId, Integer recordType, Integer pageNum, Integer pageSize);

    /**
     * 获取仓库的出入库记录
     *
     * @param warehouseId 仓库ID
     * @param recordType  记录类型（1-入库，2-出库，null-全部）
     * @param startDate   开始日期
     * @param endDate     结束日期
     * @param pageNum     页码
     * @param pageSize    每页数量
     * @return 出入库记录列表
     */
    PageResult<StockRecordDTO> getWarehouseRecords(Long warehouseId, Integer recordType, 
                                                            LocalDate startDate, LocalDate endDate, 
                                                            Integer pageNum, Integer pageSize);

    /**
     * 获取出入库统计
     *
     * @param warehouseId 仓库ID
     * @param startDate   开始日期
     * @param endDate     结束日期
     * @return 出入库统计
     */
    ResponseDTO<StockStatsDTO> getStockStats(Long warehouseId, LocalDate startDate, LocalDate endDate);
} 