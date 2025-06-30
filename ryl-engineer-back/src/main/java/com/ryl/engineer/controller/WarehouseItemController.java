package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.common.dto.PageDTO;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.InventoryStatsDTO;
import com.ryl.engineer.warehouse.dto.WarehouseItemDTO;
import com.ryl.engineer.warehouse.dto.WarehouseItemDetailDTO;
import com.ryl.engineer.warehouse.dto.request.*;
import com.ryl.engineer.warehouse.service.WarehouseItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 仓库物品管理控制器
 */
@RestController
@RequestMapping("/api/v1/warehouse/item")
public class WarehouseItemController {

    @Autowired
    private WarehouseItemService warehouseItemService;

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
    @GetMapping("/list")
    public Result<PageDTO<WarehouseItemDTO>> getItemList(
            @RequestParam Long warehouseId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ResponseDTO<PageDTO<WarehouseItemDTO>> responseDTO = warehouseItemService.getItemList(warehouseId, categoryId, keyword, pageNum, pageSize);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 获取物品详情
     *
     * @param id 物品ID
     * @return 物品详情
     */
    @GetMapping("/{id}")
    public Result<WarehouseItemDetailDTO> getItemDetail(@PathVariable Long id) {
        ResponseDTO<WarehouseItemDetailDTO> responseDTO = warehouseItemService.getItemDetail(id);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 添加物品
     *
     * @param request 添加请求
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result<Void> addItem(@RequestBody @Validated WarehouseItemAddRequest request) {
        ResponseDTO<Void> responseDTO = warehouseItemService.addItem(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 更新物品
     *
     * @param request 更新请求
     * @return 更新结果
     */
    @PutMapping("/update")
    public Result<Void> updateItem(@RequestBody @Validated WarehouseItemUpdateRequest request) {
        ResponseDTO<Void> responseDTO = warehouseItemService.updateItem(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 删除物品
     *
     * @param request 删除请求
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteItem(@RequestBody @Validated DeleteRequest request) {
        ResponseDTO<Void> responseDTO = warehouseItemService.deleteItem(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 入库操作
     *
     * @param request 入库请求
     * @return 入库结果
     */
    @PostMapping("/stock-in")
    public Result<Void> stockIn(@RequestBody @Validated StockInRequest request) {
        ResponseDTO<Void> responseDTO = warehouseItemService.stockIn(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 出库操作
     *
     * @param request 出库请求
     * @return 出库结果
     */
    @PostMapping("/stock-out")
    public Result<Void> stockOut(@RequestBody @Validated StockOutRequest request) {
        ResponseDTO<Void> responseDTO = warehouseItemService.stockOut(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 物品使用申请
     *
     * @param request 使用申请请求
     * @return 申请结果
     */
    @PostMapping("/apply-use")
    public Result<Void> applyForUse(@RequestBody @Validated ItemUseRequest request) {
        ResponseDTO<Void> responseDTO = warehouseItemService.applyForUse(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 物品采购申请
     *
     * @param request 采购申请请求
     * @return 申请结果
     */
    @PostMapping("/apply-purchase")
    public Result<Void> applyForPurchase(@RequestBody @Validated ItemPurchaseRequest request) {
        ResponseDTO<Void> responseDTO = warehouseItemService.applyForPurchase(request);
        if (responseDTO.getCode() == 200) {
            return Result.success();
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 获取库存统计
     *
     * @return 库存统计
     */
    @GetMapping("/inventory-stats")
    public Result<InventoryStatsDTO> getInventoryStats() {
        ResponseDTO<InventoryStatsDTO> responseDTO = warehouseItemService.getInventoryStats();
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 解析物品二维码
     *
     * @param request 二维码解析请求
     * @return 解析结果
     */
    @PostMapping("/parse-qrcode")
    public Result<WarehouseItemDetailDTO> parseItemQRCode(@RequestBody @Validated QRCodeParseRequest request) {
        ResponseDTO<WarehouseItemDetailDTO> responseDTO = warehouseItemService.parseItemQRCode(request);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }
} 