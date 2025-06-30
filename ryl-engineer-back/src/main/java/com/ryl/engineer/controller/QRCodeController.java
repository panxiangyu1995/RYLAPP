package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.WarehouseItemDTO;
import com.ryl.engineer.warehouse.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 二维码控制器
 */
@RestController
@RequestMapping("/api/v1/warehouse/qrcode")
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;

    /**
     * 解析物品二维码
     *
     * @param code 二维码内容
     * @return 物品信息
     */
    @GetMapping("/parse")
    public Result<WarehouseItemDTO> parseItemQRCode(@RequestParam String code) {
        ResponseDTO<WarehouseItemDTO> responseDTO = qrCodeService.parseItemQRCode(code);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }

    /**
     * 生成物品二维码
     *
     * @param itemId 物品ID
     * @return 二维码Base64字符串
     */
    @GetMapping("/generate/{itemId}")
    public Result<String> generateItemQRCode(@PathVariable Long itemId) {
        ResponseDTO<String> responseDTO = qrCodeService.generateItemQRCode(itemId);
        if (responseDTO.getCode() == 200) {
            return Result.success(responseDTO.getData());
        } else {
            return Result.error(responseDTO.getCode(), responseDTO.getMessage());
        }
    }
} 