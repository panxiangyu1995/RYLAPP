package com.ryl.engineer.warehouse.service;

import com.ryl.engineer.common.dto.PageDTO;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.InventoryStatsDTO;
import com.ryl.engineer.warehouse.dto.WarehouseItemDTO;
import com.ryl.engineer.warehouse.dto.WarehouseItemDetailDTO;
import com.ryl.engineer.warehouse.dto.request.*;

import java.util.List;

/**
 * 仓库物品服务接口
 */
public interface WarehouseItemService {

    /**
     * 获取物品列表
     *
     * @param warehouseId 仓库ID
     * @param categoryId  分类ID
     * @param keyword     关键字
     * @param pageNum     页码
     * @param pageSize    每页数量
     * @return 物品列表
     */
    ResponseDTO<PageDTO<WarehouseItemDTO>> getItemList(Long warehouseId, Long categoryId, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取物品详情
     *
     * @param itemId 物品ID
     * @return 物品详情
     */
    ResponseDTO<WarehouseItemDetailDTO> getItemDetail(Long itemId);

    /**
     * 添加物品
     *
     * @param request 物品添加请求
     * @return 添加结果
     */
    ResponseDTO<Void> addItem(WarehouseItemAddRequest request);

    /**
     * 更新物品
     *
     * @param request 物品更新请求
     * @return 更新结果
     */
    ResponseDTO<Void> updateItem(WarehouseItemUpdateRequest request);

    /**
     * 删除物品
     *
     * @param request 删除请求
     * @return 删除结果
     */
    ResponseDTO<Void> deleteItem(DeleteRequest request);

    /**
     * 入库操作
     *
     * @param request 入库请求
     * @return 入库结果
     */
    ResponseDTO<Void> stockIn(StockInRequest request);

    /**
     * 出库操作
     *
     * @param request 出库请求
     * @return 出库结果
     */
    ResponseDTO<Void> stockOut(StockOutRequest request);

    /**
     * 物品使用申请
     *
     * @param request 使用申请请求
     * @return 申请结果
     */
    ResponseDTO<Void> applyForUse(ItemUseRequest request);

    /**
     * 物品采购申请
     *
     * @param request 采购申请请求
     * @return 申请结果
     */
    ResponseDTO<Void> applyForPurchase(ItemPurchaseRequest request);

    /**
     * 获取库存统计
     *
     * @return 库存统计
     */
    ResponseDTO<InventoryStatsDTO> getInventoryStats();

    /**
     * 解析物品二维码
     *
     * @param request 二维码解析请求
     * @return 解析结果
     */
    ResponseDTO<WarehouseItemDetailDTO> parseItemQRCode(QRCodeParseRequest request);
} 