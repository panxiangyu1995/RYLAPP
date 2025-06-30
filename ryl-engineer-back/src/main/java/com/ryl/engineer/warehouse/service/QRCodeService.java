package com.ryl.engineer.warehouse.service;

import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.QRCodeResultDTO;
import com.ryl.engineer.warehouse.dto.request.QRCodeParseRequest;
import com.ryl.engineer.warehouse.dto.WarehouseItemDTO;

/**
 * 二维码服务接口
 */
public interface QRCodeService {

    /**
     * 解析物品二维码
     *
     * @param code 二维码内容
     * @return 物品信息
     */
    ResponseDTO<WarehouseItemDTO> parseItemQRCode(String code);

    /**
     * 生成物品二维码
     *
     * @param itemId 物品ID
     * @return 二维码Base64字符串
     */
    ResponseDTO<String> generateItemQRCode(Long itemId);

    /**
     * 生成仓库二维码
     *
     * @param warehouseId 仓库ID
     * @return 二维码Base64字符串
     */
    ResponseDTO<String> generateWarehouseQRCode(Long warehouseId);

    /**
     * 解析二维码
     *
     * @param request 解析请求
     * @return 解析结果
     */
    ResponseDTO<QRCodeResultDTO> parseQRCode(QRCodeParseRequest request);
} 