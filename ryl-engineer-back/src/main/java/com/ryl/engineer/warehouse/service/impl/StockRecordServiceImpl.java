package com.ryl.engineer.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.common.utils.UserContext;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.warehouse.dto.StockRecordDTO;
import com.ryl.engineer.warehouse.dto.StockStatsDTO;
import com.ryl.engineer.warehouse.dto.request.StockInRequest;
import com.ryl.engineer.warehouse.dto.request.StockOutRequest;
import com.ryl.engineer.warehouse.entity.StockRecord;
import com.ryl.engineer.warehouse.entity.WarehouseItem;
import com.ryl.engineer.warehouse.mapper.StockRecordMapper;
import com.ryl.engineer.warehouse.mapper.WarehouseItemMapper;
import com.ryl.engineer.warehouse.service.StockRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 出入库记录服务实现类
 */
@Service
public class StockRecordServiceImpl implements StockRecordService {

    @Resource
    private StockRecordMapper stockRecordMapper;

    @Resource
    private WarehouseItemMapper warehouseItemMapper;

    @Override
    @Transactional
    public ResponseDTO<Void> stockIn(StockInRequest request) {
        // 参数验证
        if (request == null) {
            return ResponseDTO.paramError("请求参数不能为空");
        }
        if (request.getItemId() == null) {
            return ResponseDTO.paramError("物品ID不能为空");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return ResponseDTO.paramError("入库数量必须大于0");
        }
        if (request.getOperationTime() == null) {
            return ResponseDTO.paramError("操作时间不能为空");
        }

        // 查询物品信息
        WarehouseItem item = warehouseItemMapper.selectById(request.getItemId());
        if (item == null) {
            return ResponseDTO.error(404, "物品不存在");
        }

        // 获取当前登录用户
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return ResponseDTO.unauthorized();
        }

        // 更新物品库存
        item.setQuantity(item.getQuantity() + request.getQuantity());
        warehouseItemMapper.updateById(item);

        // 创建入库记录
        StockRecord record = new StockRecord();
        record.setItemId(request.getItemId());
        record.setRecordType(1); // 1-入库
        record.setQuantity(request.getQuantity());
        record.setOperatorId(currentUser.getId());
        record.setOperatorName(currentUser.getName());
        record.setOperationTime(request.getOperationTime());
        record.setTaskId(request.getTaskId());
        record.setDescription(request.getDescription());
        record.setRecordCode(generateRecordCode("IN"));
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        stockRecordMapper.insert(record);

        return ResponseDTO.success(null);
    }

    @Override
    @Transactional
    public ResponseDTO<Void> stockOut(StockOutRequest request) {
        // 参数验证
        if (request == null) {
            return ResponseDTO.paramError("请求参数不能为空");
        }
        if (request.getItemId() == null) {
            return ResponseDTO.paramError("物品ID不能为空");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return ResponseDTO.paramError("出库数量必须大于0");
        }
        if (request.getOperationTime() == null) {
            return ResponseDTO.paramError("操作时间不能为空");
        }

        // 查询物品信息
        WarehouseItem item = warehouseItemMapper.selectById(request.getItemId());
        if (item == null) {
            return ResponseDTO.error(404, "物品不存在");
        }

        // 获取当前登录用户
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return ResponseDTO.unauthorized();
        }

        // 检查库存是否足够
        if (item.getQuantity() < request.getQuantity()) {
            return ResponseDTO.error(400, "库存不足");
        }

        // 更新物品库存
        item.setQuantity(item.getQuantity() - request.getQuantity());
        warehouseItemMapper.updateById(item);

        // 创建出库记录
        StockRecord record = new StockRecord();
        record.setItemId(request.getItemId());
        record.setRecordType(2); // 2-出库
        record.setQuantity(request.getQuantity());
        record.setOperatorId(currentUser.getId());
        record.setOperatorName(currentUser.getName());
        record.setOperationTime(request.getOperationTime());
        record.setTaskId(request.getTaskId());
        record.setDescription(request.getDescription());
        record.setRecordCode(generateRecordCode("OUT"));
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        stockRecordMapper.insert(record);

        return ResponseDTO.success(null);
    }

    @Override
    public PageResult<StockRecordDTO> getRecords(Long itemId, Integer recordType, Long warehouseId,
                                                         LocalDateTime startTime, LocalDateTime endTime,
                                                         Integer pageNum, Integer pageSize) {
        // 创建查询条件
        LambdaQueryWrapper<StockRecord> queryWrapper = new LambdaQueryWrapper<>();

        // 添加物品ID条件
        if (itemId != null) {
            queryWrapper.eq(StockRecord::getItemId, itemId);
        } else if (warehouseId != null) {
            // 如果没有指定物品ID但指定了仓库ID，则查询该仓库下所有物品的记录
            LambdaQueryWrapper<WarehouseItem> itemQueryWrapper = new LambdaQueryWrapper<>();
            itemQueryWrapper.eq(WarehouseItem::getWarehouseId, warehouseId);
            List<WarehouseItem> items = warehouseItemMapper.selectList(itemQueryWrapper);
            List<Long> itemIds = items.stream().map(WarehouseItem::getId).collect(Collectors.toList());

            if (itemIds.isEmpty()) {
                // 仓库中没有物品，返回空列表
                return PageResult.fromPage(new Page<>(pageNum, pageSize));
            }

            queryWrapper.in(StockRecord::getItemId, itemIds);
        }

        // 添加记录类型条件
        if (recordType != null) {
            queryWrapper.eq(StockRecord::getRecordType, recordType);
        }

        // 添加时间范围条件
        if (startTime != null) {
            queryWrapper.ge(StockRecord::getOperationTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(StockRecord::getOperationTime, endTime);
        }

        // 按操作时间降序排序
        queryWrapper.orderByDesc(StockRecord::getOperationTime);

        // 分页查询
        Page<StockRecord> page = new Page<>(pageNum, pageSize);
        IPage<StockRecord> recordPage = stockRecordMapper.selectPage(page, queryWrapper);

        // 转换为DTO并返回
        return PageResult.fromPage(recordPage.convert(this::convertToDTO));
    }

    /**
     * 生成记录编号
     * 
     * @param prefix 前缀（IN-入库，OUT-出库）
     * @return 记录编号
     */
    private String generateRecordCode(String prefix) {
        // 格式：前缀-年份-序号，如：IN-2024-001
        String year = String.valueOf(LocalDate.now().getYear());
        
        // 获取当前年份的最大序号
        Integer maxSequence = stockRecordMapper.getMaxSequence(prefix, year);
        int sequence = maxSequence == null ? 1 : maxSequence + 1;
        
        // 格式化序号为三位数
        String sequenceStr = String.format("%03d", sequence);
        
        return prefix + "-" + year + "-" + sequenceStr;
    }

    @Override
    public PageResult<StockRecordDTO> getItemRecords(Long itemId, Integer recordType, Integer pageNum, Integer pageSize) {
        // 创建查询条件
        LambdaQueryWrapper<StockRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StockRecord::getItemId, itemId);
        if (recordType != null) {
            queryWrapper.eq(StockRecord::getRecordType, recordType);
        }

        // 按操作时间降序排序
        queryWrapper.orderByDesc(StockRecord::getOperationTime);

        // 分页查询
        Page<StockRecord> page = new Page<>(pageNum, pageSize);
        IPage<StockRecord> recordPage = stockRecordMapper.selectPage(page, queryWrapper);

        // 转换为DTO并返回
        return PageResult.fromPage(recordPage.convert(this::convertToDTO));
    }

    @Override
    public PageResult<StockRecordDTO> getWarehouseRecords(Long warehouseId, Integer recordType,
                                                                   LocalDate startDate, LocalDate endDate,
                                                                   Integer pageNum, Integer pageSize) {
        // 创建查询条件
        LambdaQueryWrapper<StockRecord> queryWrapper = new LambdaQueryWrapper<>();

        // 获取仓库下的所有物品ID
        LambdaQueryWrapper<WarehouseItem> itemQueryWrapper = new LambdaQueryWrapper<>();
        itemQueryWrapper.eq(WarehouseItem::getWarehouseId, warehouseId);
        List<WarehouseItem> items = warehouseItemMapper.selectList(itemQueryWrapper);
        List<Long> itemIds = items.stream().map(WarehouseItem::getId).collect(Collectors.toList());

        if (itemIds.isEmpty()) {
            return PageResult.fromPage(new Page<>(pageNum, pageSize));
        }
        queryWrapper.in(StockRecord::getItemId, itemIds);

        // 添加记录类型条件
        if (recordType != null) {
            queryWrapper.eq(StockRecord::getRecordType, recordType);
        }

        // 添加时间范围条件
        if (startDate != null) {
            queryWrapper.ge(StockRecord::getOperationTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            queryWrapper.le(StockRecord::getOperationTime, endDate.atTime(LocalTime.MAX));
        }

        // 按操作时间降序排序
        queryWrapper.orderByDesc(StockRecord::getOperationTime);

        // 分页查询
        Page<StockRecord> page = new Page<>(pageNum, pageSize);
        IPage<StockRecord> recordPage = stockRecordMapper.selectPage(page, queryWrapper);

        // 转换为DTO并返回
        return PageResult.fromPage(recordPage.convert(this::convertToDTO));
    }

    @Override
    public ResponseDTO<StockStatsDTO> getStockStats(Long warehouseId, LocalDate startDate, LocalDate endDate) {
        // 参数验证
        if (startDate == null || endDate == null) {
            return ResponseDTO.paramError("开始日期和结束日期不能为空");
        }
        
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
        
        // 创建统计结果对象
        StockStatsDTO statsDTO = new StockStatsDTO();
        
        // 查询入库统计
        Map<String, Object> inStats = stockRecordMapper.countInStats(startDateTime, endDateTime);
        List<StockStatsDTO.StockDateStatsDTO> inStatsList = new ArrayList<>();
        if (inStats != null) {
            StockStatsDTO.StockDateStatsDTO inStatItem = new StockStatsDTO.StockDateStatsDTO();
            inStatItem.setDate(startDate.toString() + " - " + endDate.toString());
            inStatItem.setCount(inStats.get("count") != null ? ((Number) inStats.get("count")).intValue() : 0);
            inStatItem.setQuantity(inStats.get("quantity") != null ? ((Number) inStats.get("quantity")).intValue() : 0);
            inStatItem.setValue(inStats.get("value") != null ? BigDecimal.valueOf(((Number) inStats.get("value")).doubleValue()) : BigDecimal.ZERO);
            inStatsList.add(inStatItem);
        }
        statsDTO.setInStats(inStatsList);
        
        // 查询出库统计
        Map<String, Object> outStats = stockRecordMapper.countOutStats(startDateTime, endDateTime);
        List<StockStatsDTO.StockDateStatsDTO> outStatsList = new ArrayList<>();
        if (outStats != null) {
            StockStatsDTO.StockDateStatsDTO outStatItem = new StockStatsDTO.StockDateStatsDTO();
            outStatItem.setDate(startDate.toString() + " - " + endDate.toString());
            outStatItem.setCount(outStats.get("count") != null ? ((Number) outStats.get("count")).intValue() : 0);
            outStatItem.setQuantity(outStats.get("quantity") != null ? ((Number) outStats.get("quantity")).intValue() : 0);
            outStatItem.setValue(outStats.get("value") != null ? BigDecimal.valueOf(((Number) outStats.get("value")).doubleValue()) : BigDecimal.ZERO);
            outStatsList.add(outStatItem);
        }
        statsDTO.setOutStats(outStatsList);
        
        // 查询按分类统计的数据
        List<Map<String, Object>> inStatsByCategory = stockRecordMapper.countInStatsByCategory(startDateTime, endDateTime);
        List<Map<String, Object>> outStatsByCategory = stockRecordMapper.countOutStatsByCategory(startDateTime, endDateTime);
        
        // 合并入库和出库统计
        Map<Long, StockStatsDTO.CategoryStatsDTO> categoryStatsMap = new HashMap<>();
        
        // 处理入库统计
        if (inStatsByCategory != null) {
            for (Map<String, Object> stat : inStatsByCategory) {
                Long categoryId = stat.get("category_id") != null ? ((Number) stat.get("category_id")).longValue() : null;
                if (categoryId == null) continue;
                
                StockStatsDTO.CategoryStatsDTO categoryStats = categoryStatsMap.computeIfAbsent(categoryId, k -> new StockStatsDTO.CategoryStatsDTO());
                categoryStats.setCategoryId(categoryId);
                categoryStats.setInCount(stat.get("count") != null ? ((Number) stat.get("count")).intValue() : 0);
                categoryStats.setInQuantity(stat.get("quantity") != null ? ((Number) stat.get("quantity")).intValue() : 0);
                categoryStats.setInValue(stat.get("value") != null ? BigDecimal.valueOf(((Number) stat.get("value")).doubleValue()) : BigDecimal.ZERO);
            }
        }
        
        // 处理出库统计
        if (outStatsByCategory != null) {
            for (Map<String, Object> stat : outStatsByCategory) {
                Long categoryId = stat.get("category_id") != null ? ((Number) stat.get("category_id")).longValue() : null;
                if (categoryId == null) continue;
                
                StockStatsDTO.CategoryStatsDTO categoryStats = categoryStatsMap.computeIfAbsent(categoryId, k -> new StockStatsDTO.CategoryStatsDTO());
                categoryStats.setCategoryId(categoryId);
                categoryStats.setOutCount(stat.get("count") != null ? ((Number) stat.get("count")).intValue() : 0);
                categoryStats.setOutQuantity(stat.get("quantity") != null ? ((Number) stat.get("quantity")).intValue() : 0);
                categoryStats.setOutValue(stat.get("value") != null ? BigDecimal.valueOf(((Number) stat.get("value")).doubleValue()) : BigDecimal.ZERO);
            }
        }
        
        statsDTO.setCategoryStats(new ArrayList<>(categoryStatsMap.values()));
        
        return ResponseDTO.success(statsDTO);
    }
    
    /**
     * 将实体转换为DTO
     */
    private StockRecordDTO convertToDTO(StockRecord record) {
        if (record == null) {
            return null;
        }
        
        StockRecordDTO dto = new StockRecordDTO();
        BeanUtils.copyProperties(record, dto);
        
        // 查询物品信息
        WarehouseItem item = warehouseItemMapper.selectById(record.getItemId());
        if (item != null) {
            dto.setItemCode(item.getItemCode());
            dto.setItemName(item.getName());
        }
        
        return dto;
    }
} 