package com.ryl.engineer.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.request.DeleteRequest;
import com.ryl.engineer.warehouse.entity.ItemCategory;
import com.ryl.engineer.warehouse.entity.WarehouseItem;
import com.ryl.engineer.warehouse.mapper.ItemCategoryMapper;
import com.ryl.engineer.warehouse.mapper.WarehouseItemMapper;
import com.ryl.engineer.warehouse.service.ItemCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 物品分类服务实现类
 */
@Service
public class ItemCategoryServiceImpl implements ItemCategoryService {

    @Resource
    private ItemCategoryMapper itemCategoryMapper;

    @Resource
    private WarehouseItemMapper warehouseItemMapper;

    @Override
    public ResponseDTO<List<ItemCategory>> getAllCategories() {
        // 查询所有分类
        LambdaQueryWrapper<ItemCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ItemCategory::getId);
        List<ItemCategory> categoryList = itemCategoryMapper.selectList(queryWrapper);
        
        return ResponseDTO.success(categoryList);
    }

    @Override
    public ResponseDTO<ItemCategory> getCategoryDetail(Long categoryId) {
        // 查询分类信息
        ItemCategory category = itemCategoryMapper.selectById(categoryId);
        if (category == null) {
            return ResponseDTO.error(404, "分类不存在");
        }
        
        return ResponseDTO.success(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Void> addCategory(ItemCategory category) {
        // 检查分类名称是否已存在
        LambdaQueryWrapper<ItemCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ItemCategory::getName, category.getName());
        if (itemCategoryMapper.selectCount(queryWrapper) > 0) {
            return ResponseDTO.error(400, "分类名称已存在");
        }
        
        // 设置创建和更新时间
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        
        // 保存分类
        itemCategoryMapper.insert(category);
        
        return ResponseDTO.success(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Void> updateCategory(ItemCategory category) {
        // 检查分类是否存在
        ItemCategory existCategory = itemCategoryMapper.selectById(category.getId());
        if (existCategory == null) {
            return ResponseDTO.error(404, "分类不存在");
        }
        
        // 检查分类名称是否已存在（排除自身）
        LambdaQueryWrapper<ItemCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ItemCategory::getName, category.getName())
                .ne(ItemCategory::getId, category.getId());
        if (itemCategoryMapper.selectCount(queryWrapper) > 0) {
            return ResponseDTO.error(400, "分类名称已存在");
        }
        
        // 设置更新时间
        category.setUpdateTime(LocalDateTime.now());
        // 保留创建时间
        category.setCreateTime(existCategory.getCreateTime());
        
        // 保存更新
        itemCategoryMapper.updateById(category);
        
        return ResponseDTO.success(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Void> deleteCategory(DeleteRequest request) {
        // 检查参数
        if (request == null || request.getId() == null) {
            return ResponseDTO.paramError("参数错误");
        }
        
        // 检查分类是否存在
        ItemCategory category = itemCategoryMapper.selectById(request.getId());
        if (category == null) {
            return ResponseDTO.error(404, "分类不存在");
        }
        
        // 检查分类是否有物品
        LambdaQueryWrapper<WarehouseItem> itemQueryWrapper = new LambdaQueryWrapper<>();
        itemQueryWrapper.eq(WarehouseItem::getCategoryId, request.getId());
        if (warehouseItemMapper.selectCount(itemQueryWrapper) > 0) {
            return ResponseDTO.error(400, "分类下存在物品，无法删除");
        }
        
        // 删除分类
        itemCategoryMapper.deleteById(request.getId());
        
        return ResponseDTO.success(null);
    }
} 