package com.ryl.engineer.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.QRCodeResultDTO;
import com.ryl.engineer.warehouse.dto.WarehouseItemDTO;
import com.ryl.engineer.warehouse.dto.request.QRCodeParseRequest;
import com.ryl.engineer.warehouse.entity.ItemCategory;
import com.ryl.engineer.warehouse.entity.Warehouse;
import com.ryl.engineer.warehouse.entity.WarehouseItem;
import com.ryl.engineer.warehouse.mapper.ItemCategoryMapper;
import com.ryl.engineer.warehouse.mapper.WarehouseItemMapper;
import com.ryl.engineer.warehouse.mapper.WarehouseMapper;
import com.ryl.engineer.warehouse.service.QRCodeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 二维码服务实现类
 */
@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Resource
    private WarehouseMapper warehouseMapper;

    @Resource
    private ItemCategoryMapper itemCategoryMapper;

    @Resource
    private WarehouseItemMapper warehouseItemMapper;

    @Override
    public ResponseDTO<WarehouseItemDTO> parseItemQRCode(String code) {
        // 参数验证
        if (!StringUtils.hasText(code)) {
            return ResponseDTO.paramError("二维码内容不能为空");
        }

        // 解析二维码内容
        // 格式：仓库代码 + 分类代码 + 物品编号，例如：A1INS-2024-001
        
        // 使用正则表达式解析
        Pattern pattern = Pattern.compile("([A-Z])([1-4])(.+)");
        Matcher matcher = pattern.matcher(code);
        
        if (!matcher.matches() || matcher.groupCount() != 3) {
            return ResponseDTO.paramError("二维码格式不正确");
        }
        
        String warehouseCode = matcher.group(1);
        String categoryCode = matcher.group(2);
        String itemCode = matcher.group(3);
        
        // 查询物品
        LambdaQueryWrapper<WarehouseItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WarehouseItem::getItemCode, itemCode);
        WarehouseItem item = warehouseItemMapper.selectOne(queryWrapper);
        
        if (item == null) {
            return ResponseDTO.paramError("物品不存在");
        }
        
        // 查询分类
        ItemCategory category = itemCategoryMapper.selectById(item.getCategoryId());
        
        // 转换为DTO
        WarehouseItemDTO itemDTO = WarehouseItemDTO.fromEntity(item);
        // 设置分类名称
        if (category != null) {
            itemDTO.setCategoryName(category.getName());
        }
        
        return ResponseDTO.success(itemDTO);
    }

    @Override
    public ResponseDTO<String> generateItemQRCode(Long itemId) {
        // 参数验证
        if (itemId == null) {
            return ResponseDTO.paramError("物品ID不能为空");
        }
        
        // 查询物品
        WarehouseItem item = warehouseItemMapper.selectById(itemId);
        if (item == null) {
            return ResponseDTO.error(404, "物品不存在");
        }
        
        // 查询仓库
        Warehouse warehouse = warehouseMapper.selectById(item.getWarehouseId());
        if (warehouse == null) {
            return ResponseDTO.error(404, "仓库不存在");
        }
        
        // 查询分类
        ItemCategory category = itemCategoryMapper.selectById(item.getCategoryId());
        if (category == null) {
            return ResponseDTO.error(404, "分类不存在");
        }
        
        // 生成二维码内容
        String warehouseCode = getWarehouseCode(warehouse.getId());
        String categoryCode = String.valueOf(category.getId());
        String qrContent = warehouseCode + categoryCode + item.getItemCode();
        
        // 注意：这里只返回二维码内容，实际生成二维码图片的逻辑应该在前端实现
        // 如果需要在后端生成二维码图片，可以使用ZXing等库
        
        return ResponseDTO.success(qrContent);
    }

    @Override
    public ResponseDTO<String> generateWarehouseQRCode(Long warehouseId) {
        // 参数验证
        if (warehouseId == null) {
            return ResponseDTO.paramError("仓库ID不能为空");
        }
        
        // 查询仓库
        Warehouse warehouse = warehouseMapper.selectById(warehouseId);
        if (warehouse == null) {
            return ResponseDTO.error(404, "仓库不存在");
        }
        
        // 生成二维码内容
        String warehouseCode = getWarehouseCode(warehouse.getId());
        String qrContent = warehouseCode + "-" + warehouse.getCode();
        
        // 注意：这里只返回二维码内容，实际生成二维码图片的逻辑应该在前端实现
        
        return ResponseDTO.success(qrContent);
    }

    @Override
    public ResponseDTO<QRCodeResultDTO> parseQRCode(QRCodeParseRequest request) {
        // 参数验证
        if (request == null || !StringUtils.hasText(request.getContent())) {
            return ResponseDTO.paramError("二维码内容不能为空");
        }

        // 解析二维码内容
        // 格式：仓库代码 + 分类代码 + 物品编号，例如：A1INS-2024-001
        String content = request.getContent();
        
        // 使用正则表达式解析
        Pattern pattern = Pattern.compile("([A-Z])([1-4])(.+)");
        Matcher matcher = pattern.matcher(content);
        
        if (!matcher.matches() || matcher.groupCount() != 3) {
            return ResponseDTO.paramError("二维码格式不正确");
        }
        
        String warehouseCode = matcher.group(1);
        String categoryCode = matcher.group(2);
        String itemCode = matcher.group(3);
        
        // 查询仓库
        Warehouse warehouse = null;
        if ("A".equals(warehouseCode)) {
            warehouse = warehouseMapper.selectById(1L); // 泰州仓
        } else if ("B".equals(warehouseCode)) {
            warehouse = warehouseMapper.selectById(2L); // 苏州仓
        } else if ("C".equals(warehouseCode)) {
            warehouse = warehouseMapper.selectById(3L); // 武汉仓
        } else if ("D".equals(warehouseCode)) {
            warehouse = warehouseMapper.selectById(4L); // 四川仓
        }
        
        if (warehouse == null) {
            return ResponseDTO.paramError("无效的仓库代码");
        }
        
        // 查询分类
        Long categoryId = Long.parseLong(categoryCode);
        ItemCategory category = itemCategoryMapper.selectById(categoryId);
        if (category == null) {
            return ResponseDTO.paramError("无效的分类代码");
        }
        
        // 查询物品
        LambdaQueryWrapper<WarehouseItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WarehouseItem::getItemCode, itemCode);
        WarehouseItem item = warehouseItemMapper.selectOne(queryWrapper);
        
        if (item == null) {
            return ResponseDTO.paramError("物品不存在");
        }
        
        // 构建结果
        QRCodeResultDTO resultDTO = new QRCodeResultDTO();
        resultDTO.setWarehouseCode(warehouseCode);
        resultDTO.setWarehouseName(warehouse.getName());
        resultDTO.setCategoryCode(categoryCode);
        resultDTO.setCategoryName(category.getName());
        resultDTO.setItemCode(itemCode);
        
        // 使用工具方法转换为DTO
        WarehouseItemDTO itemDTO = WarehouseItemDTO.fromEntity(item);
        // 设置分类名称
        itemDTO.setCategoryName(category.getName());
        
        resultDTO.setItem(itemDTO);
        
        return ResponseDTO.success(resultDTO);
    }
    
    /**
     * 根据仓库ID获取仓库代码
     */
    private String getWarehouseCode(Long warehouseId) {
        if (warehouseId == 1L) {
            return "A"; // 泰州仓
        } else if (warehouseId == 2L) {
            return "B"; // 苏州仓
        } else if (warehouseId == 3L) {
            return "C"; // 武汉仓
        } else if (warehouseId == 4L) {
            return "D"; // 四川仓
        }
        return "X"; // 未知仓库
    }
} 