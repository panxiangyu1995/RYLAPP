package com.ryl.engineer.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.InventoryCheckDTO;
import com.ryl.engineer.warehouse.dto.InventoryCheckDetailDTO;
import com.ryl.engineer.warehouse.dto.request.InventoryCheckCreateRequest;
import com.ryl.engineer.warehouse.dto.request.InventoryCheckSubmitRequest;
import com.ryl.engineer.warehouse.entity.InventoryCheck;
import com.ryl.engineer.warehouse.entity.InventoryCheckDetail;
import com.ryl.engineer.warehouse.entity.Warehouse;
import com.ryl.engineer.warehouse.entity.WarehouseItem;
import com.ryl.engineer.warehouse.mapper.InventoryCheckDetailMapper;
import com.ryl.engineer.warehouse.mapper.InventoryCheckMapper;
import com.ryl.engineer.warehouse.mapper.WarehouseItemMapper;
import com.ryl.engineer.warehouse.mapper.WarehouseMapper;
import com.ryl.engineer.warehouse.service.InventoryCheckService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 盘库服务实现类
 */
@Service
public class InventoryCheckServiceImpl implements InventoryCheckService {

    @Resource
    private InventoryCheckMapper inventoryCheckMapper;
    
    @Resource
    private InventoryCheckDetailMapper inventoryCheckDetailMapper;
    
    @Resource
    private WarehouseMapper warehouseMapper;
    
    @Resource
    private WarehouseItemMapper warehouseItemMapper;

    @Override
    public PageResult<InventoryCheckDTO> getCheckList(Long warehouseId, Integer status, Integer pageNum, Integer pageSize) {
        // 创建查询条件
        LambdaQueryWrapper<InventoryCheck> queryWrapper = new LambdaQueryWrapper<>();
        if (warehouseId != null) {
            queryWrapper.eq(InventoryCheck::getWarehouseId, warehouseId);
        }
        if (status != null) {
            queryWrapper.eq(InventoryCheck::getStatus, status);
        }
        queryWrapper.orderByDesc(InventoryCheck::getCreateTime);

        // 分页查询
        Page<InventoryCheck> page = new Page<>(pageNum, pageSize);
        IPage<InventoryCheck> checkPage = inventoryCheckMapper.selectPage(page, queryWrapper);

        // 转换为DTO
        IPage<InventoryCheckDTO> dtoPage = checkPage.convert(check -> {
            InventoryCheckDTO dto = InventoryCheckDTO.fromEntity(check);
            // 查询仓库名称
            Warehouse warehouse = warehouseMapper.selectById(check.getWarehouseId());
            if (warehouse != null) {
                dto.setWarehouseName(warehouse.getName());
            }
            // 查询盘点物品数量和差异物品数量
            dto.setItemCount(inventoryCheckDetailMapper.countByCheckId(check.getId()));
            dto.setDifferenceCount(inventoryCheckDetailMapper.countDifferenceByCheckId(check.getId()));
            return dto;
        });

        return PageResult.fromPage(dtoPage);
    }

    @Override
    public PageResult<InventoryCheckDTO> getUserCheckList(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        // 创建查询条件
        LambdaQueryWrapper<InventoryCheck> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryCheck::getCheckerId, userId);
        if (status != null) {
            queryWrapper.eq(InventoryCheck::getStatus, status);
        }
        queryWrapper.orderByDesc(InventoryCheck::getCreateTime);

        // 分页查询
        Page<InventoryCheck> page = new Page<>(pageNum, pageSize);
        IPage<InventoryCheck> checkPage = inventoryCheckMapper.selectPage(page, queryWrapper);

        // 转换为DTO
        IPage<InventoryCheckDTO> dtoPage = checkPage.convert(check -> {
            InventoryCheckDTO dto = InventoryCheckDTO.fromEntity(check);
            // 查询仓库名称
            Warehouse warehouse = warehouseMapper.selectById(check.getWarehouseId());
            if (warehouse != null) {
                dto.setWarehouseName(warehouse.getName());
            }
            // 查询盘点物品数量和差异物品数量
            dto.setItemCount(inventoryCheckDetailMapper.countByCheckId(check.getId()));
            dto.setDifferenceCount(inventoryCheckDetailMapper.countDifferenceByCheckId(check.getId()));
            return dto;
        });

        return PageResult.fromPage(dtoPage);
    }

    @Override
    public ResponseDTO<InventoryCheckDTO> getCheckDetail(Long checkId) {
        // 参数验证
        if (checkId == null) {
            return ResponseDTO.paramError("盘库ID不能为空");
        }
        
        // 查询盘库信息
        InventoryCheck check = inventoryCheckMapper.selectById(checkId);
        if (check == null) {
            return ResponseDTO.error(404, "盘库记录不存在");
        }
        
        // 转换为DTO
        InventoryCheckDTO checkDTO = InventoryCheckDTO.fromEntity(check);
        
        // 查询仓库名称
        Warehouse warehouse = warehouseMapper.selectById(check.getWarehouseId());
        if (warehouse != null) {
            checkDTO.setWarehouseName(warehouse.getName());
        }
        
        // 查询盘点物品数量和差异物品数量
        // 由于没有实现countByCheckId和countDifferenceByCheckId方法，这里使用查询方式计算
        LambdaQueryWrapper<InventoryCheckDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryCheckDetail::getCheckId, checkId);
        Integer itemCount = Math.toIntExact(inventoryCheckDetailMapper.countByCheckId(checkId));
        
        LambdaQueryWrapper<InventoryCheckDetail> diffQueryWrapper = new LambdaQueryWrapper<>();
        diffQueryWrapper.eq(InventoryCheckDetail::getCheckId, checkId);
        diffQueryWrapper.apply("system_quantity != actual_quantity");
        Integer differenceCount = Math.toIntExact(inventoryCheckDetailMapper.countDifferenceByCheckId(checkId));
        
        checkDTO.setItemCount(itemCount);
        checkDTO.setDifferenceCount(differenceCount);
        
        return ResponseDTO.success(checkDTO);
    }

    @Override
    public PageResult<InventoryCheckDetailDTO> getCheckDetailList(Long checkId, Integer pageNum, Integer pageSize) {
        // 创建查询条件
        LambdaQueryWrapper<InventoryCheckDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryCheckDetail::getCheckId, checkId);

        // 分页查询
        Page<InventoryCheckDetail> page = new Page<>(pageNum, pageSize);
        IPage<InventoryCheckDetail> detailPage = inventoryCheckDetailMapper.selectPage(page, queryWrapper);

        // 转换为DTO
        IPage<InventoryCheckDetailDTO> detailDTOPage = detailPage.convert(detail -> {
            InventoryCheckDetailDTO dto = new InventoryCheckDetailDTO();
            BeanUtils.copyProperties(detail, dto);

            // 查询物品信息
            WarehouseItem item = warehouseItemMapper.selectById(detail.getItemId());
            if (item != null) {
                dto.setItemName(item.getName());
                dto.setModel(item.getModel());
                // 设置其他物品信息
            }

            return dto;
        });

        return PageResult.fromPage(detailDTOPage);
    }

    @Override
    public PageResult<InventoryCheckDetailDTO> getDifferenceDetailList(Long checkId, Integer pageNum, Integer pageSize) {
        // 创建查询条件
        LambdaQueryWrapper<InventoryCheckDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryCheckDetail::getCheckId, checkId);
        queryWrapper.apply("system_quantity != actual_quantity"); // 期望数量不等于实际数量

        // 分页查询
        Page<InventoryCheckDetail> page = new Page<>(pageNum, pageSize);
        IPage<InventoryCheckDetail> detailPage = inventoryCheckDetailMapper.selectPage(page, queryWrapper);

        // 转换为DTO
        IPage<InventoryCheckDetailDTO> detailDTOPage = detailPage.convert(detail -> {
            InventoryCheckDetailDTO dto = new InventoryCheckDetailDTO();
            BeanUtils.copyProperties(detail, dto);

            // 查询物品信息
            WarehouseItem item = warehouseItemMapper.selectById(detail.getItemId());
            if (item != null) {
                dto.setItemName(item.getName());
                dto.setModel(item.getModel());
                // 设置其他物品信息
            }
            return dto;
        });

        return PageResult.fromPage(detailDTOPage);
    }

    @Override
    @Transactional
    public ResponseDTO<Long> createInventoryCheck(InventoryCheckCreateRequest request) {
        // 参数验证
        if (request == null) {
            return ResponseDTO.paramError("请求参数不能为空");
        }
        
        // 检查仓库是否存在
        Warehouse warehouse = warehouseMapper.selectById(request.getWarehouseId());
        if (warehouse == null) {
            return ResponseDTO.error(404, "仓库不存在");
        }
        
        // 创建盘库记录
        InventoryCheck check = new InventoryCheck();
        BeanUtils.copyProperties(request, check);
        
        // 生成盘库编号
        String checkCode = generateCheckCode();
        check.setCheckCode(checkCode);
        
        // 设置状态和时间
        check.setStatus(0); // 待盘点
        LocalDateTime now = LocalDateTime.now();
        check.setCreateTime(now);
        check.setUpdateTime(now);
        
        // 保存盘库记录
        inventoryCheckMapper.insert(check);
        
        // 查询仓库中的物品
        LambdaQueryWrapper<WarehouseItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WarehouseItem::getWarehouseId, request.getWarehouseId());
        List<WarehouseItem> items = warehouseItemMapper.selectList(queryWrapper);
        
        // 创建盘库明细
        for (WarehouseItem item : items) {
            InventoryCheckDetail detail = new InventoryCheckDetail();
            detail.setCheckId(check.getId());
            detail.setItemId(item.getId());
            detail.setSystemQuantity(item.getQuantity());
            detail.setActualQuantity(0); // 初始化为0，等待盘点
            detail.setDifference(item.getQuantity()); // 差异 = 系统数量 - 实际数量
            detail.setCreateTime(now);
            detail.setUpdateTime(now);
            inventoryCheckDetailMapper.insert(detail);
        }
        
        return ResponseDTO.success(check.getId());
    }

    @Override
    @Transactional
    public ResponseDTO<Void> startInventoryCheck(Long checkId) {
        // 参数验证
        if (checkId == null) {
            return ResponseDTO.paramError("盘库ID不能为空");
        }
        
        // 查询盘库记录
        InventoryCheck check = inventoryCheckMapper.selectById(checkId);
        if (check == null) {
            return ResponseDTO.error(404, "盘库记录不存在");
        }
        
        // 检查状态
        if (check.getStatus() != 0) {
            return ResponseDTO.error(400, "盘库记录状态不正确，无法开始盘点");
        }
        
        // 更新状态
        check.setStatus(1); // 盘点中
        check.setUpdateTime(LocalDateTime.now());
        inventoryCheckMapper.updateById(check);
        
        return ResponseDTO.success(null);
    }

    @Override
    @Transactional
    public ResponseDTO<Void> submitInventoryCheck(InventoryCheckSubmitRequest request) {
        // 参数验证
        if (request == null) {
            return ResponseDTO.paramError("请求参数不能为空");
        }
        
        // 查询盘库记录
        InventoryCheck check = inventoryCheckMapper.selectById(request.getId());
        if (check == null) {
            return ResponseDTO.error(404, "盘库记录不存在");
        }
        
        // 检查状态
        if (check.getStatus() != 1) {
            return ResponseDTO.error(400, "盘库记录状态不正确，无法提交盘点结果");
        }
        
        // 更新盘库明细
        LocalDateTime now = LocalDateTime.now();
        for (InventoryCheckSubmitRequest.InventoryCheckDetailRequest detailRequest : request.getDetails()) {
            // 查询盘库明细
            LambdaQueryWrapper<InventoryCheckDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(InventoryCheckDetail::getCheckId, request.getId());
            queryWrapper.eq(InventoryCheckDetail::getItemId, detailRequest.getItemId());
            InventoryCheckDetail detail = inventoryCheckDetailMapper.selectOne(queryWrapper);
            
            if (detail != null) {
                // 更新实际数量和备注
                detail.setActualQuantity(detailRequest.getActualQuantity());
                detail.setDifference(detail.getSystemQuantity() - detailRequest.getActualQuantity());
                detail.setRemark(detailRequest.getRemark());
                detail.setUpdateTime(now);
                inventoryCheckDetailMapper.updateById(detail);
            } else {
                // 如果不存在，创建新的盘库明细
                InventoryCheckDetail newDetail = new InventoryCheckDetail();
                newDetail.setCheckId(request.getId());
                newDetail.setItemId(detailRequest.getItemId());
                
                // 查询物品的预期数量
                WarehouseItem item = warehouseItemMapper.selectById(detailRequest.getItemId());
                if (item != null) {
                    newDetail.setSystemQuantity(item.getQuantity());
                    newDetail.setDifference(item.getQuantity() - detailRequest.getActualQuantity());
                } else {
                    newDetail.setSystemQuantity(0);
                    newDetail.setDifference(-detailRequest.getActualQuantity());
                }
                
                newDetail.setActualQuantity(detailRequest.getActualQuantity());
                newDetail.setRemark(detailRequest.getRemark());
                newDetail.setCreateTime(now);
                newDetail.setUpdateTime(now);
                inventoryCheckDetailMapper.insert(newDetail);
            }
        }
        
        return ResponseDTO.success(null);
    }

    @Override
    @Transactional
    public ResponseDTO<Void> completeInventoryCheck(Long checkId, String remark) {
        // 参数验证
        if (checkId == null) {
            return ResponseDTO.paramError("盘库ID不能为空");
        }
        
        // 查询盘库记录
        InventoryCheck check = inventoryCheckMapper.selectById(checkId);
        if (check == null) {
            return ResponseDTO.error(404, "盘库记录不存在");
        }
        
        // 检查状态
        if (check.getStatus() != 1) {
            return ResponseDTO.error(400, "盘库记录状态不正确，无法完成盘点");
        }
        
        // 检查是否所有物品都已盘点
        LambdaQueryWrapper<InventoryCheckDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryCheckDetail::getCheckId, checkId);
        queryWrapper.eq(InventoryCheckDetail::getActualQuantity, 0);
        Integer unCheckedCount = Math.toIntExact(inventoryCheckDetailMapper.selectCount(queryWrapper));
        
        if (unCheckedCount > 0) {
            return ResponseDTO.error(400, "还有" + unCheckedCount + "个物品未盘点，无法完成盘点");
        }
        
        // 更新状态和备注
        check.setStatus(2); // 已完成
        if (remark != null && !remark.isEmpty()) {
            check.setDescription(remark);
        }
        check.setUpdateTime(LocalDateTime.now());
        inventoryCheckMapper.updateById(check);
        
        // 更新物品库存（根据实际盘点结果）
        List<InventoryCheckDetail> details = inventoryCheckDetailMapper.selectList(
                new LambdaQueryWrapper<InventoryCheckDetail>()
                        .eq(InventoryCheckDetail::getCheckId, checkId));
        
        for (InventoryCheckDetail detail : details) {
            // 如果实际数量与预期数量不一致，更新物品库存
            if (!detail.getActualQuantity().equals(detail.getSystemQuantity())) {
                WarehouseItem item = warehouseItemMapper.selectById(detail.getItemId());
                if (item != null) {
                    item.setQuantity(detail.getActualQuantity());
                    item.setUpdateTime(LocalDateTime.now());
                    warehouseItemMapper.updateById(item);
                }
            }
        }
        
        return ResponseDTO.success(null);
    }
    
    /**
     * 生成盘库编号
     *
     * @return 盘库编号
     */
    private String generateCheckCode() {
        // 获取当前日期
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.getYear() + String.format("%02d", now.getMonthValue()) + String.format("%02d", now.getDayOfMonth());
        
        // 获取序列号（由于没有实现getNextSequence方法，这里使用时间戳后4位）
        String sequenceStr = String.format("%04d", (int)(System.currentTimeMillis() % 10000));
        
        return "IC-" + dateStr + "-" + sequenceStr;
    }
} 