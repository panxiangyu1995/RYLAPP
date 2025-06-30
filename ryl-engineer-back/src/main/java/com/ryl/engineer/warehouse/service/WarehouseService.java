package com.ryl.engineer.warehouse.service;

import com.ryl.engineer.common.dto.PageDTO;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.WarehouseDTO;
import com.ryl.engineer.warehouse.dto.WarehouseDetailDTO;
import com.ryl.engineer.warehouse.dto.WarehouseStatisticsDTO;
import com.ryl.engineer.warehouse.dto.request.DeleteRequest;
import com.ryl.engineer.warehouse.dto.request.WarehouseAddRequest;
import com.ryl.engineer.warehouse.dto.request.WarehouseUpdateRequest;

import java.util.List;

/**
 * 仓库服务接口
 */
public interface WarehouseService {

    /**
     * 获取仓库列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 仓库列表
     */
    ResponseDTO<PageDTO<WarehouseDTO>> getWarehouseList(Integer pageNum, Integer pageSize);

    /**
     * 获取所有仓库
     *
     * @return 所有仓库列表
     */
    ResponseDTO<List<WarehouseDTO>> getAllWarehouses();

    /**
     * 获取仓库详情
     *
     * @param warehouseId 仓库ID
     * @return 仓库详情
     */
    ResponseDTO<WarehouseDetailDTO> getWarehouseDetail(Long warehouseId);

    /**
     * 获取仓库统计信息
     *
     * @param warehouseId 仓库ID
     * @return 仓库统计信息
     */
    ResponseDTO<WarehouseStatisticsDTO> getWarehouseStatistics(Long warehouseId);

    /**
     * 添加仓库
     *
     * @param request 仓库添加请求
     * @return 添加结果
     */
    ResponseDTO<Void> addWarehouse(WarehouseAddRequest request);

    /**
     * 更新仓库
     *
     * @param request 仓库更新请求
     * @return 更新结果
     */
    ResponseDTO<Void> updateWarehouse(WarehouseUpdateRequest request);

    /**
     * 删除仓库
     *
     * @param request 删除请求
     * @return 删除结果
     */
    ResponseDTO<Void> deleteWarehouse(DeleteRequest request);
} 