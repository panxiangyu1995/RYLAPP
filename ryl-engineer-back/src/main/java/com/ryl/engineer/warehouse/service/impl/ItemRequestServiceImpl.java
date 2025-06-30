package com.ryl.engineer.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryl.engineer.common.dto.PageDTO;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.ItemRequestDTO;
import com.ryl.engineer.warehouse.dto.ItemUsageStatsDTO;
import com.ryl.engineer.warehouse.dto.request.RequestProcessRequest;
import com.ryl.engineer.warehouse.entity.ItemRequest;
import com.ryl.engineer.warehouse.entity.StockRecord;
import com.ryl.engineer.warehouse.entity.WarehouseItem;
import com.ryl.engineer.warehouse.mapper.ItemRequestMapper;
import com.ryl.engineer.warehouse.mapper.StockRecordMapper;
import com.ryl.engineer.warehouse.mapper.WarehouseItemMapper;
import com.ryl.engineer.warehouse.service.ItemRequestService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物品申请服务实现类
 */
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    @Resource
    private ItemRequestMapper itemRequestMapper;
    
    @Resource
    private WarehouseItemMapper warehouseItemMapper;
    
    @Resource
    private StockRecordMapper stockRecordMapper;

    @Override
    public ResponseDTO<PageDTO<ItemRequestDTO>> getRequestList(Integer requestType, Integer status, Integer pageNum, Integer pageSize) {
        // 创建查询条件
        LambdaQueryWrapper<ItemRequest> queryWrapper = new LambdaQueryWrapper<>();
        if (requestType != null) {
            queryWrapper.eq(ItemRequest::getRequestType, requestType);
        }
        if (status != null) {
            queryWrapper.eq(ItemRequest::getStatus, status);
        }
        queryWrapper.orderByDesc(ItemRequest::getCreateTime);
        
        // 分页查询
        Page<ItemRequest> page = new Page<>(pageNum, pageSize);
        Page<ItemRequest> requestPage = itemRequestMapper.selectPage(page, queryWrapper);
        
        // 转换为DTO
        List<ItemRequestDTO> requestDTOList = requestPage.getRecords().stream()
                .map(ItemRequestDTO::fromEntity)
                .collect(Collectors.toList());
        
        // 组装分页结果
        PageDTO<ItemRequestDTO> pageDTO = new PageDTO<>();
        pageDTO.setList(requestDTOList);
        pageDTO.setTotal(requestPage.getTotal());
        pageDTO.setPages((int) requestPage.getPages());
        pageDTO.setCurrent((int) requestPage.getCurrent());
        pageDTO.setSize((int) requestPage.getSize());
        
        return ResponseDTO.success(pageDTO);
    }

    @Override
    public ResponseDTO<PageDTO<ItemRequestDTO>> getUserRequestList(Long userId, Integer requestType, Integer status, Integer pageNum, Integer pageSize) {
        // 参数验证
        if (userId == null) {
            return ResponseDTO.paramError("用户ID不能为空");
        }
        
        // 创建查询条件
        LambdaQueryWrapper<ItemRequest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ItemRequest::getRequesterId, userId);
        if (requestType != null) {
            queryWrapper.eq(ItemRequest::getRequestType, requestType);
        }
        if (status != null) {
            queryWrapper.eq(ItemRequest::getStatus, status);
        }
        queryWrapper.orderByDesc(ItemRequest::getCreateTime);
        
        // 分页查询
        Page<ItemRequest> page = new Page<>(pageNum, pageSize);
        Page<ItemRequest> requestPage = itemRequestMapper.selectPage(page, queryWrapper);
        
        // 转换为DTO
        List<ItemRequestDTO> requestDTOList = requestPage.getRecords().stream()
                .map(ItemRequestDTO::fromEntity)
                .collect(Collectors.toList());
        
        // 组装分页结果
        PageDTO<ItemRequestDTO> pageDTO = new PageDTO<>();
        pageDTO.setList(requestDTOList);
        pageDTO.setTotal(requestPage.getTotal());
        pageDTO.setPages((int) requestPage.getPages());
        pageDTO.setCurrent((int) requestPage.getCurrent());
        pageDTO.setSize((int) requestPage.getSize());
        
        return ResponseDTO.success(pageDTO);
    }

    @Override
    public ResponseDTO<PageDTO<ItemRequestDTO>> getTaskRequestList(String taskId, Integer pageNum, Integer pageSize) {
        // 参数验证
        if (taskId == null || taskId.isEmpty()) {
            return ResponseDTO.paramError("任务ID不能为空");
        }
        
        // 创建查询条件
        LambdaQueryWrapper<ItemRequest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ItemRequest::getTaskId, taskId);
        queryWrapper.orderByDesc(ItemRequest::getCreateTime);
        
        // 分页查询
        Page<ItemRequest> page = new Page<>(pageNum, pageSize);
        Page<ItemRequest> requestPage = itemRequestMapper.selectPage(page, queryWrapper);
        
        // 转换为DTO
        List<ItemRequestDTO> requestDTOList = requestPage.getRecords().stream()
                .map(ItemRequestDTO::fromEntity)
                .collect(Collectors.toList());
        
        // 组装分页结果
        PageDTO<ItemRequestDTO> pageDTO = new PageDTO<>();
        pageDTO.setList(requestDTOList);
        pageDTO.setTotal(requestPage.getTotal());
        pageDTO.setPages((int) requestPage.getPages());
        pageDTO.setCurrent((int) requestPage.getCurrent());
        pageDTO.setSize((int) requestPage.getSize());
        
        return ResponseDTO.success(pageDTO);
    }

    @Override
    public ResponseDTO<ItemRequestDTO> getRequestDetail(Long requestId) {
        // 参数验证
        if (requestId == null) {
            return ResponseDTO.paramError("申请ID不能为空");
        }
        
        // 查询申请信息
        ItemRequest request = itemRequestMapper.selectById(requestId);
        if (request == null) {
            return ResponseDTO.error(404, "申请不存在");
        }
        
        // 转换为DTO
        ItemRequestDTO requestDTO = ItemRequestDTO.fromEntity(request);
        
        return ResponseDTO.success(requestDTO);
    }

    @Override
    @Transactional
    public ResponseDTO<Void> processRequest(RequestProcessRequest request) {
        // 参数验证
        if (request == null) {
            return ResponseDTO.paramError("请求参数不能为空");
        }
        
        // 查询申请信息
        ItemRequest itemRequest = itemRequestMapper.selectById(request.getId());
        if (itemRequest == null) {
            return ResponseDTO.error(404, "申请不存在");
        }
        
        // 检查状态
        if (itemRequest.getStatus() != 0) {
            return ResponseDTO.error(400, "申请已处理，不能重复处理");
        }
        
        // 更新申请状态
        itemRequest.setStatus(request.getStatus());
        itemRequest.setApprovalComment(request.getComment());
        itemRequest.setApprovalTime(LocalDateTime.now());
        itemRequest.setUpdateTime(LocalDateTime.now());
        itemRequestMapper.updateById(itemRequest);
        
        // 如果是同意使用申请，则自动出库
        if (request.getStatus() == 1 && itemRequest.getRequestType() == 1) {
            // 检查物品是否存在
            WarehouseItem item = warehouseItemMapper.selectById(itemRequest.getItemId());
            if (item == null) {
                return ResponseDTO.error(404, "物品不存在");
            }
            
            // 检查库存是否足够
            if (item.getQuantity() < itemRequest.getQuantity()) {
                return ResponseDTO.error(400, "库存不足");
            }
            
            // 更新库存数量
            item.setQuantity(item.getQuantity() - itemRequest.getQuantity());
            item.setUpdateTime(LocalDateTime.now());
            warehouseItemMapper.updateById(item);
            
            // 创建出库记录
            StockRecord stockRecord = new StockRecord();
            stockRecord.setItemId(itemRequest.getItemId());
            stockRecord.setRecordType(2); // 出库
            stockRecord.setQuantity(itemRequest.getQuantity());
            stockRecord.setOperationTime(LocalDateTime.now());
            stockRecord.setTaskId(itemRequest.getTaskId());
            stockRecord.setDescription("申请使用出库，申请ID：" + itemRequest.getId());
            stockRecord.setCreateTime(LocalDateTime.now());
            stockRecord.setUpdateTime(LocalDateTime.now());
            stockRecordMapper.insert(stockRecord);
        }
        
        return ResponseDTO.success(null);
    }

    @Override
    public ResponseDTO<ItemUsageStatsDTO> getItemUsageStats(LocalDate startDate, LocalDate endDate) {
        // 参数验证
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // 获取统计数据
        ItemUsageStatsDTO statsDTO = new ItemUsageStatsDTO();
        
        // 由于ItemRequestMapper中没有实现getTopItems方法，这里模拟实现
        List<ItemUsageStatsDTO.TopItemDTO> topItems = new ArrayList<>();
        // 这里应该通过SQL查询获取使用量最多的物品，但由于没有实现，暂时返回空列表
        statsDTO.setTopItems(topItems);
        
        // 由于ItemRequestMapper中没有实现getTaskStats方法，这里模拟实现
        List<ItemUsageStatsDTO.TaskStatsDTO> taskStats = new ArrayList<>();
        // 这里应该通过SQL查询获取按任务统计的数据，但由于没有实现，暂时返回空列表
        statsDTO.setTaskStats(taskStats);
        
        // 由于ItemRequestMapper中没有实现getUserStats方法，这里模拟实现
        List<ItemUsageStatsDTO.UserStatsDTO> userStats = new ArrayList<>();
        // 这里应该通过SQL查询获取按用户统计的数据，但由于没有实现，暂时返回空列表
        statsDTO.setUserStats(userStats);
        
        return ResponseDTO.success(statsDTO);
    }
} 