package com.ryl.engineer.warehouse.service;

import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.request.DeleteRequest;
import com.ryl.engineer.warehouse.entity.ItemCategory;

import java.util.List;

/**
 * 物品分类服务接口
 */
public interface ItemCategoryService {

    /**
     * 获取所有物品分类
     *
     * @return 物品分类列表
     */
    ResponseDTO<List<ItemCategory>> getAllCategories();

    /**
     * 获取分类详情
     *
     * @param categoryId 分类ID
     * @return 分类详情
     */
    ResponseDTO<ItemCategory> getCategoryDetail(Long categoryId);

    /**
     * 添加物品分类
     *
     * @param category 物品分类
     * @return 添加结果
     */
    ResponseDTO<Void> addCategory(ItemCategory category);

    /**
     * 更新物品分类
     *
     * @param category 物品分类
     * @return 更新结果
     */
    ResponseDTO<Void> updateCategory(ItemCategory category);

    /**
     * 删除物品分类
     *
     * @param request 删除请求
     * @return 删除结果
     */
    ResponseDTO<Void> deleteCategory(DeleteRequest request);
} 