package com.ryl.engineer.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryl.engineer.common.dto.PageDTO;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.WarehouseDTO;
import com.ryl.engineer.warehouse.dto.WarehouseDetailDTO;
import com.ryl.engineer.warehouse.dto.WarehouseStatisticsDTO;
import com.ryl.engineer.warehouse.dto.request.DeleteRequest;
import com.ryl.engineer.warehouse.dto.request.WarehouseAddRequest;
import com.ryl.engineer.warehouse.dto.request.WarehouseUpdateRequest;
import com.ryl.engineer.warehouse.entity.Warehouse;
import com.ryl.engineer.warehouse.entity.WarehouseItem;
import com.ryl.engineer.warehouse.mapper.WarehouseItemMapper;
import com.ryl.engineer.warehouse.mapper.WarehouseMapper;
import com.ryl.engineer.warehouse.service.WarehouseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 仓库服务实现类
 */
@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Resource
    private WarehouseMapper warehouseMapper;

    @Resource
    private WarehouseItemMapper warehouseItemMapper;

    @Override
    public ResponseDTO<PageDTO<WarehouseDTO>> getWarehouseList(Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Page<Warehouse> page = new Page<>(pageNum, pageSize);
        // 查询条件
        LambdaQueryWrapper<Warehouse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Warehouse::getId);
        // 执行查询
        Page<Warehouse> warehousePage = warehouseMapper.selectPage(page, queryWrapper);
        
        // 转换为DTO
        List<WarehouseDTO> warehouseDTOList = warehousePage.getRecords().stream().map(warehouse -> {
            return WarehouseDTO.fromEntity(warehouse);
        }).collect(Collectors.toList());
        
        // 组装分页结果
        PageDTO<WarehouseDTO> pageDTO = new PageDTO<>();
        pageDTO.setList(warehouseDTOList);
        pageDTO.setTotal(warehousePage.getTotal());
        pageDTO.setPages((int) warehousePage.getPages());
        pageDTO.setCurrent((int) warehousePage.getCurrent());
        pageDTO.setSize((int) warehousePage.getSize());
        
        return ResponseDTO.success(pageDTO);
    }

    @Override
    public ResponseDTO<List<WarehouseDTO>> getAllWarehouses() {
        // 查询所有仓库
        LambdaQueryWrapper<Warehouse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Warehouse::getId);
        List<Warehouse> warehouseList = warehouseMapper.selectList(queryWrapper);
        
        // 转换为DTO
        List<WarehouseDTO> warehouseDTOList = warehouseList.stream().map(warehouse -> {
            return WarehouseDTO.fromEntity(warehouse);
        }).collect(Collectors.toList());
        
        return ResponseDTO.success(warehouseDTOList);
    }

    @Override
    public ResponseDTO<WarehouseDetailDTO> getWarehouseDetail(Long warehouseId) {
        // 查询仓库信息
        Warehouse warehouse = warehouseMapper.selectById(warehouseId);
        if (warehouse == null) {
            return ResponseDTO.error(404, "仓库不存在");
        }
        
        // 转换为DTO
        WarehouseDetailDTO warehouseDetailDTO = WarehouseDetailDTO.fromEntity(warehouse);
        
        // 查询仓库统计信息
        WarehouseStatisticsDTO statisticsDTO = new WarehouseStatisticsDTO();
        
        // 统计各类物品数量
        int totalItems = warehouseItemMapper.countByWarehouseId(warehouseId);
        statisticsDTO.setTotalItems(totalItems);
        
        // 设置统计信息
        warehouseDetailDTO.setStatistics(statisticsDTO);
        
        return ResponseDTO.success(warehouseDetailDTO);
    }

    @Override
    public ResponseDTO<WarehouseStatisticsDTO> getWarehouseStatistics(Long warehouseId) {
        // 查询仓库信息
        Warehouse warehouse = warehouseMapper.selectById(warehouseId);
        if (warehouse == null) {
            return ResponseDTO.error(404, "仓库不存在");
        }
        
        // 组装统计结果
        WarehouseStatisticsDTO statisticsDTO = new WarehouseStatisticsDTO();
        
        // 统计各类物品数量
        int totalItems = warehouseItemMapper.countByWarehouseId(warehouseId);
        statisticsDTO.setTotalItems(totalItems);
        
        return ResponseDTO.success(statisticsDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Void> addWarehouse(WarehouseAddRequest request) {
        // 检查仓库名称是否已存在
        LambdaQueryWrapper<Warehouse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Warehouse::getName, request.getName());
        if (warehouseMapper.selectCount(queryWrapper) > 0) {
            return ResponseDTO.error(400, "仓库名称已存在");
        }
        
        // 创建仓库对象
        Warehouse warehouse = new Warehouse();
        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        warehouse.setDescription(request.getDescription());
        warehouse.setStatus(request.getStatus());
        warehouse.setCreateTime(LocalDateTime.now());
        warehouse.setUpdateTime(LocalDateTime.now());
        
        // 保存仓库
        warehouseMapper.insert(warehouse);
        
        return ResponseDTO.success(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Void> updateWarehouse(WarehouseUpdateRequest request) {
        // 检查仓库是否存在
        Warehouse warehouse = warehouseMapper.selectById(request.getId());
        if (warehouse == null) {
            return ResponseDTO.error(404, "仓库不存在");
        }
        
        // 检查仓库名称是否已存在（排除自身）
        LambdaQueryWrapper<Warehouse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Warehouse::getName, request.getName())
                .ne(Warehouse::getId, request.getId());
        if (warehouseMapper.selectCount(queryWrapper) > 0) {
            return ResponseDTO.error(400, "仓库名称已存在");
        }
        
        // 更新仓库信息
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        warehouse.setDescription(request.getDescription());
        warehouse.setStatus(request.getStatus());
        warehouse.setUpdateTime(LocalDateTime.now());
        
        // 保存更新
        warehouseMapper.updateById(warehouse);
        
        return ResponseDTO.success(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Void> deleteWarehouse(DeleteRequest request) {
        // 检查仓库是否存在
        Warehouse warehouse = warehouseMapper.selectById(request.getId());
        if (warehouse == null) {
            return ResponseDTO.error(404, "仓库不存在");
        }
        
        // 检查仓库是否有物品
        LambdaQueryWrapper<WarehouseItem> itemQueryWrapper = new LambdaQueryWrapper<>();
        itemQueryWrapper.eq(WarehouseItem::getWarehouseId, request.getId());
        if (warehouseItemMapper.selectCount(itemQueryWrapper) > 0) {
            return ResponseDTO.error(400, "仓库中存在物品，无法删除");
        }
        
        // 删除仓库
        warehouseMapper.deleteById(request.getId());
        
        return ResponseDTO.success(null);
    }
} 