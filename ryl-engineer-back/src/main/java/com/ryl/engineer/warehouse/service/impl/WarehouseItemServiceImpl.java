package com.ryl.engineer.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.InventoryStatsDTO;
import com.ryl.engineer.warehouse.dto.WarehouseItemDTO;
import com.ryl.engineer.warehouse.dto.WarehouseItemDetailDTO;
import com.ryl.engineer.warehouse.dto.StockRecordDTO;
import com.ryl.engineer.warehouse.dto.request.*;
import com.ryl.engineer.warehouse.entity.*;
import com.ryl.engineer.warehouse.mapper.*;
import com.ryl.engineer.warehouse.service.WarehouseItemService;
import com.ryl.engineer.service.UserService;
import com.ryl.engineer.service.PermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.ryl.engineer.common.exception.PermissionDeniedException;

/**
 * 仓库物品服务实现类
 */
@Service
public class WarehouseItemServiceImpl implements WarehouseItemService {

    @Resource
    private WarehouseItemMapper warehouseItemMapper;

    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private UserService userService;

    @Resource
    private ItemCategoryMapper itemCategoryMapper;
    
    @Resource
    private WarehouseMapper warehouseMapper;
    
    @Resource
    private InstrumentDetailsMapper instrumentDetailsMapper;
    
    @Resource
    private PartDetailsMapper partDetailsMapper;
    
    @Resource
    private ConsumableDetailsMapper consumableDetailsMapper;
    
    @Resource
    private AssetDetailsMapper assetDetailsMapper;
    
    @Resource
    private StockRecordMapper stockRecordMapper;
    
    @Resource
    private ItemRequestMapper itemRequestMapper;
    
    @Override
    public PageResult<WarehouseItemDTO> getItemList(Long warehouseId, Long categoryId, String keyword, Integer pageNum, Integer pageSize) {
        // 创建查询条件
        LambdaQueryWrapper<WarehouseItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WarehouseItem::getWarehouseId, warehouseId);
        if (categoryId != null) {
            queryWrapper.eq(WarehouseItem::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(WarehouseItem::getName, keyword)
                    .or()
                    .like(WarehouseItem::getModel, keyword)
                    .or()
                    .like(WarehouseItem::getManufacturer, keyword)
                    .or()
                    .like(WarehouseItem::getItemCode, keyword)
            );
        }

        // 分页查询
        Page<WarehouseItem> page = new Page<>(pageNum, pageSize);
        IPage<WarehouseItem> itemPage = warehouseItemMapper.selectPage(page, queryWrapper);

        // 转换为DTO
        IPage<WarehouseItemDTO> itemDTOPage = itemPage.convert(item -> {
            WarehouseItemDTO dto = WarehouseItemDTO.fromEntity(item);
            // 查询分类名称
            ItemCategory category = itemCategoryMapper.selectById(item.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            return dto;
        });

        // 组装分页结果
        return PageResult.fromPage(itemDTOPage);
    }

    @Override
    public ResponseDTO<WarehouseItemDetailDTO> getItemDetail(Long itemId) {
        // 参数验证
        if (itemId == null) {
            return ResponseDTO.paramError("物品ID不能为空");
        }
        
        // 查询物品信息
        WarehouseItem item = warehouseItemMapper.selectById(itemId);
        if (item == null) {
            return ResponseDTO.error(404, "物品不存在");
        }
        
        // 转换为DTO
        WarehouseItemDetailDTO detailDTO = new WarehouseItemDetailDTO();
        BeanUtils.copyProperties(item, detailDTO);
        
        // 查询分类名称
        ItemCategory category = itemCategoryMapper.selectById(item.getCategoryId());
        if (category != null) {
            detailDTO.setCategoryName(category.getName());
        }
        
        // 查询仓库名称
        Warehouse warehouse = warehouseMapper.selectById(item.getWarehouseId());
        if (warehouse != null) {
            detailDTO.setWarehouseName(warehouse.getName());
        }
        
        // 根据分类ID查询不同的详情
        Map<String, Object> detailsMap = new HashMap<>();
        if (item.getCategoryId() == 1) {
            // 仪器库
            InstrumentDetails details = instrumentDetailsMapper.selectById(itemId);
            if (details != null) {
                detailsMap.put("isNew", details.getIsNew());
                detailsMap.put("productionDate", details.getProductionDate());
                detailsMap.put("recoveryUnit", details.getRecoveryUnit());
            }
        } else if (item.getCategoryId() == 2) {
            // 配件库
            PartDetails details = partDetailsMapper.selectById(itemId);
            if (details != null) {
                detailsMap.put("partNumber", details.getPartNumber());
            }
        } else if (item.getCategoryId() == 3) {
            // 耗材库
            ConsumableDetails details = consumableDetailsMapper.selectById(itemId);
            if (details != null) {
                detailsMap.put("partNumber", details.getPartNumber());
            }
        } else if (item.getCategoryId() == 4) {
            // 固定资产库
            AssetDetails details = assetDetailsMapper.selectById(itemId);
            if (details != null) {
                // 固定资产没有额外字段，这里可以添加一些基础信息
                detailsMap.put("itemId", details.getItemId());
                detailsMap.put("createTime", details.getCreateTime());
            }
        }
        detailDTO.setDetails(detailsMap);
        
        // 查询最近的出入库记录
        List<StockRecord> stockRecords = stockRecordMapper.getRecentRecords(itemId, 5);
        // 将 StockRecord 转换为 StockRecordDTO
        List<StockRecordDTO> stockRecordDTOs = stockRecords.stream()
                .map(record -> {
                    StockRecordDTO dto = new StockRecordDTO();
                    BeanUtils.copyProperties(record, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        detailDTO.setStockRecords(stockRecordDTOs);
        
        return ResponseDTO.success(detailDTO);
    }
    
    @Override
    @Transactional
    public ResponseDTO<Void> addItem(WarehouseItemAddRequest request) {
        // 参数验证
        if (request == null) {
            return ResponseDTO.paramError("请求参数不能为空");
        }
        
        // 检查仓库是否存在
        Warehouse warehouse = warehouseMapper.selectById(request.getWarehouseId());
        if (warehouse == null) {
            return ResponseDTO.error(404, "仓库不存在");
        }
        
        // 检查分类是否存在
        ItemCategory category = itemCategoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            return ResponseDTO.error(404, "分类不存在");
        }
        
        // 创建物品实体
        WarehouseItem item = new WarehouseItem();
        BeanUtils.copyProperties(request, item);
        
        // 生成物品编号
        String itemCode = generateItemCode(request.getCategoryId());
        item.setItemCode(itemCode);
        
        // 设置状态和时间
        item.setStatus(1); // 启用状态
        LocalDateTime now = LocalDateTime.now();
        item.setCreateTime(now);
        item.setUpdateTime(now);
        
        // 保存物品信息
        warehouseItemMapper.insert(item);
        
        // 保存详细信息
        if (request.getDetails() != null && !request.getDetails().isEmpty()) {
            saveItemDetails(item.getId(), request.getCategoryId(), request.getDetails());
        }
        
        // 创建入库记录
        StockRecord stockRecord = new StockRecord();
        stockRecord.setItemId(item.getId());
        stockRecord.setRecordType(1); // 入库
        stockRecord.setQuantity(request.getQuantity());
        stockRecord.setOperationTime(now);
        stockRecord.setDescription("初始入库");
        stockRecord.setCreateTime(now);
        stockRecord.setUpdateTime(now);
        stockRecordMapper.insert(stockRecord);
        
        return ResponseDTO.success(null);
    }
    
    @Override
    @Transactional
    public ResponseDTO<Void> updateItem(WarehouseItemUpdateRequest request) {
        // 参数验证
        if (request == null) {
            return ResponseDTO.paramError("请求参数不能为空");
        }
        
        // 检查物品是否存在
        WarehouseItem existingItem = warehouseItemMapper.selectById(request.getId());
        if (existingItem == null) {
            return ResponseDTO.error(404, "物品不存在");
        }
        
        // 检查仓库是否存在
        Warehouse warehouse = warehouseMapper.selectById(request.getWarehouseId());
        if (warehouse == null) {
            return ResponseDTO.error(404, "仓库不存在");
        }
        
        // 检查分类是否存在
        ItemCategory category = itemCategoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            return ResponseDTO.error(404, "分类不存在");
        }
        
        // 更新物品信息
        BeanUtils.copyProperties(request, existingItem);
        existingItem.setUpdateTime(LocalDateTime.now());
        warehouseItemMapper.updateById(existingItem);
        
        // 更新详细信息
        if (request.getDetails() != null && !request.getDetails().isEmpty()) {
            updateItemDetails(existingItem.getId(), request.getCategoryId(), request.getDetails());
        }
        
        return ResponseDTO.success(null);
    }
    
    @Override
    @Transactional
    public ResponseDTO<Void> deleteItem(DeleteRequest request) {
        // 1. 权限检查
        permissionService.checkWarehouseDeletePermission();

        // 2. 验证安全密码
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userService.verifySecurityPassword(username, request.getPassword())) {
            throw new PermissionDeniedException("安全密码错误。");
        }

        // 3. 参数验证
        if (request == null || request.getId() == null) {
            return ResponseDTO.paramError("物品ID不能为空");
        }
        
        // 4. 业务逻辑
        WarehouseItem item = warehouseItemMapper.selectById(request.getId());
        if (item == null) {
            return ResponseDTO.error(404, "物品不存在");
        }
        
        warehouseItemMapper.deleteById(request.getId());
        deleteItemDetails(request.getId(), item.getCategoryId());
        
        LambdaQueryWrapper<StockRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StockRecord::getItemId, request.getId());
        stockRecordMapper.delete(queryWrapper);
        
        return ResponseDTO.success(null);
    }
    
    @Override
    @Transactional
    public ResponseDTO<Void> stockIn(StockInRequest request) {
        // 参数验证
        if (request == null) {
            return ResponseDTO.paramError("请求参数不能为空");
        }
        
        // 检查物品是否存在
        WarehouseItem item = warehouseItemMapper.selectById(request.getItemId());
        if (item == null) {
            return ResponseDTO.error(404, "物品不存在");
        }
        
        // 更新库存数量
        item.setQuantity(item.getQuantity() + request.getQuantity());
        item.setUpdateTime(LocalDateTime.now());
        warehouseItemMapper.updateById(item);
        
        // 创建入库记录
        StockRecord stockRecord = new StockRecord();
        stockRecord.setItemId(request.getItemId());
        stockRecord.setRecordType(1); // 入库
        stockRecord.setQuantity(request.getQuantity());
        stockRecord.setOperationTime(request.getOperationTime());
        stockRecord.setTaskId(request.getTaskId());
        stockRecord.setDescription(request.getDescription());
        stockRecord.setCreateTime(LocalDateTime.now());
        stockRecord.setUpdateTime(LocalDateTime.now());
        stockRecordMapper.insert(stockRecord);
        
        return ResponseDTO.success(null);
    }
    
    @Override
    @Transactional
    public ResponseDTO<Void> stockOut(StockOutRequest request) {
        // 参数验证
        if (request == null) {
            return ResponseDTO.paramError("请求参数不能为空");
        }
        
        // 检查物品是否存在
        WarehouseItem item = warehouseItemMapper.selectById(request.getItemId());
        if (item == null) {
            return ResponseDTO.error(404, "物品不存在");
        }
        
        // 检查库存是否足够
        if (item.getQuantity() < request.getQuantity()) {
            return ResponseDTO.error(400, "库存不足");
        }
        
        // 更新库存数量
        item.setQuantity(item.getQuantity() - request.getQuantity());
        item.setUpdateTime(LocalDateTime.now());
        warehouseItemMapper.updateById(item);
        
        // 创建出库记录
        StockRecord stockRecord = new StockRecord();
        stockRecord.setItemId(request.getItemId());
        stockRecord.setRecordType(2); // 出库
        stockRecord.setQuantity(request.getQuantity());
        stockRecord.setOperationTime(request.getOperationTime());
        stockRecord.setTaskId(request.getTaskId());
        stockRecord.setDescription(request.getDescription());
        stockRecord.setCreateTime(LocalDateTime.now());
        stockRecord.setUpdateTime(LocalDateTime.now());
        stockRecordMapper.insert(stockRecord);
        
        return ResponseDTO.success(null);
    }
    
    @Override
    @Transactional
    public ResponseDTO<Void> applyForUse(ItemUseRequest request) {
        // 参数验证
        if (request == null) {
            return ResponseDTO.paramError("请求参数不能为空");
        }
        
        // 检查物品是否存在
        WarehouseItem item = warehouseItemMapper.selectById(request.getItemId());
        if (item == null) {
            return ResponseDTO.error(404, "物品不存在");
        }
        
        // 创建物品申请记录
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setItemId(request.getItemId());
        itemRequest.setRequestType(1); // 使用申请
        itemRequest.setQuantity(request.getQuantity());
        // 使用purpose字段作为申请原因
        itemRequest.setPurpose(request.getPurpose());
        // 使用urgency字段作为紧急程度
        itemRequest.setUrgency(request.getUrgency());
        itemRequest.setTaskId(request.getTaskId());
        itemRequest.setStatus(0); // 待审批
        itemRequest.setCreateTime(LocalDateTime.now());
        itemRequest.setUpdateTime(LocalDateTime.now());
        itemRequestMapper.insert(itemRequest);
        
        return ResponseDTO.success(null);
    }
    
    @Override
    @Transactional
    public ResponseDTO<Void> applyForPurchase(ItemPurchaseRequest request) {
        // 参数验证
        if (request == null) {
            return ResponseDTO.paramError("请求参数不能为空");
        }
        
        // 创建物品申请记录
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestType(2); // 采购申请
        itemRequest.setQuantity(request.getQuantity());
        // 使用purpose字段作为申请原因
        itemRequest.setPurpose(request.getPurpose());
        // 使用urgency字段作为紧急程度
        itemRequest.setUrgency(request.getUrgency());
        itemRequest.setStatus(0); // 待审批
        itemRequest.setCreateTime(LocalDateTime.now());
        itemRequest.setUpdateTime(LocalDateTime.now());
        
        // 设置采购信息
        itemRequest.setItemName(request.getItemName());
        itemRequest.setItemModel(request.getItemModel());
        // 没有对应的字段，暂时不设置
        // itemRequest.setManufacturer(request.getItemModel());
        // itemRequest.setEstimatedPrice(BigDecimal.ZERO);
        
        itemRequestMapper.insert(itemRequest);
        
        return ResponseDTO.success(null);
    }
    
    @Override
    public ResponseDTO<InventoryStatsDTO> getInventoryStats() {
        InventoryStatsDTO statsDTO = new InventoryStatsDTO();
        
        // 获取物品总数和总价值
        Long totalItems = warehouseItemMapper.selectCount(null);
        BigDecimal totalValue = warehouseItemMapper.calculateTotalValue();
        statsDTO.setTotalItems(totalItems.intValue());
        statsDTO.setTotalValue(totalValue);
        
        // 获取分类统计
        List<InventoryStatsDTO.CategoryStatsDTO> categoryStats = warehouseItemMapper.getCategoryStats();
        statsDTO.setCategoryStats(categoryStats);
        
        // 获取仓库统计
        List<InventoryStatsDTO.WarehouseStatsDTO> warehouseStats = warehouseItemMapper.getWarehouseStats();
        statsDTO.setWarehouseStats(warehouseStats);
        
        return ResponseDTO.success(statsDTO);
    }
    
    @Override
    public ResponseDTO<WarehouseItemDetailDTO> parseItemQRCode(QRCodeParseRequest request) {
        // 参数验证
        if (request == null || request.getContent() == null || request.getContent().isEmpty()) {
            return ResponseDTO.paramError("二维码内容不能为空");
        }
        
        // 解析二维码
        String qrCode = request.getContent();
        
        // 假设二维码格式为：ITEM_CODE:XXXX
        if (!qrCode.startsWith("ITEM_CODE:")) {
            return ResponseDTO.error(400, "无效的二维码格式");
        }
        
        String itemCode = qrCode.substring("ITEM_CODE:".length());
        
        // 根据物品编号查询物品
        LambdaQueryWrapper<WarehouseItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WarehouseItem::getItemCode, itemCode);
        WarehouseItem item = warehouseItemMapper.selectOne(queryWrapper);
        
        if (item == null) {
            return ResponseDTO.error(404, "物品不存在");
        }
        
        // 获取物品详情
        return getItemDetail(item.getId());
    }
    
    /**
     * 生成物品编号
     *
     * @param categoryId 分类ID
     * @return 物品编号
     */
    private String generateItemCode(Long categoryId) {
        // 获取分类前缀
        String prefix;
        if (categoryId == 1) {
            prefix = "INS"; // 仪器
        } else if (categoryId == 2) {
            prefix = "PRT"; // 配件
        } else if (categoryId == 3) {
            prefix = "CON"; // 耗材
        } else if (categoryId == 4) {
            prefix = "AST"; // 固定资产
        } else {
            prefix = "ITM"; // 其他物品
        }
        
        // 获取当前日期
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.getYear() + String.format("%02d", now.getMonthValue());
        
        // 获取序列号
        Integer sequence = warehouseItemMapper.getNextSequence(categoryId);
        String sequenceStr = String.format("%04d", sequence);
        
        return prefix + "-" + dateStr + "-" + sequenceStr;
    }
    
    /**
     * 保存物品详情
     *
     * @param itemId     物品ID
     * @param categoryId 分类ID
     * @param details    详情数据
     */
    private void saveItemDetails(Long itemId, Long categoryId, Map<String, Object> details) {
        if (categoryId == 1) {
            // 仪器库
            InstrumentDetails instrumentDetails = new InstrumentDetails();
            instrumentDetails.setId(itemId);
            instrumentDetails.setIsNew((Integer) details.get("isNew"));
            instrumentDetails.setProductionDate((LocalDate) details.get("productionDate"));
            instrumentDetails.setRecoveryUnit((String) details.get("recoveryUnit"));
            instrumentDetailsMapper.insert(instrumentDetails);
        } else if (categoryId == 2) {
            // 配件库
            PartDetails partDetails = new PartDetails();
            partDetails.setId(itemId);
            partDetails.setPartNumber((String) details.get("partNumber"));
            partDetailsMapper.insert(partDetails);
        } else if (categoryId == 3) {
            // 耗材库
            ConsumableDetails consumableDetails = new ConsumableDetails();
            consumableDetails.setId(itemId);
            consumableDetails.setPartNumber((String) details.get("partNumber"));
            consumableDetailsMapper.insert(consumableDetails);
        } else if (categoryId == 4) {
            // 固定资产库
            AssetDetails assetDetails = new AssetDetails();
            assetDetails.setId(itemId);
            assetDetailsMapper.insert(assetDetails);
        }
    }
    
    /**
     * 更新物品详情
     *
     * @param itemId     物品ID
     * @param categoryId 分类ID
     * @param details    详情数据
     */
    private void updateItemDetails(Long itemId, Long categoryId, Map<String, Object> details) {
        // 先删除旧的详情
        deleteItemDetails(itemId, categoryId);
        
        // 再保存新的详情
        saveItemDetails(itemId, categoryId, details);
    }
    
    /**
     * 删除物品详情
     *
     * @param itemId     物品ID
     * @param categoryId 分类ID
     */
    private void deleteItemDetails(Long itemId, Long categoryId) {
        if (categoryId == 1) {
            // 仪器库
            instrumentDetailsMapper.deleteById(itemId);
        } else if (categoryId == 2) {
            // 配件库
            partDetailsMapper.deleteById(itemId);
        } else if (categoryId == 3) {
            // 耗材库
            consumableDetailsMapper.deleteById(itemId);
        } else if (categoryId == 4) {
            // 固定资产库
            assetDetailsMapper.deleteById(itemId);
        }
    }
} 