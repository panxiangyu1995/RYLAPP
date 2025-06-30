package com.ryl.engineer.controller;

import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.WarehouseItemDTO;
import com.ryl.engineer.warehouse.service.QRCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * QRCodeController单元测试
 */
public class QRCodeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QRCodeService qrCodeService;

    @InjectMocks
    private QRCodeController qrCodeController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(qrCodeController).build();
    }

    @Test
    public void testParseItemQRCode() throws Exception {
        // 准备测试数据
        WarehouseItemDTO itemDTO = new WarehouseItemDTO();
        itemDTO.setId(1L);
        itemDTO.setName("测试物品");
        itemDTO.setItemCode("INS-2024-001");
        itemDTO.setQuantity(10);
        itemDTO.setCost(new BigDecimal("100.00"));
        itemDTO.setCategoryName("仪器");

        // Mock Service层返回结果
        ResponseDTO<WarehouseItemDTO> responseDTO = ResponseDTO.success(itemDTO);
        when(qrCodeService.parseItemQRCode(anyString())).thenReturn(responseDTO);

        // 执行测试
        mockMvc.perform(get("/api/v1/warehouse/qrcode/parse")
                .param("code", "A1INS-2024-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("测试物品"))
                .andExpect(jsonPath("$.data.itemCode").value("INS-2024-001"))
                .andExpect(jsonPath("$.data.categoryName").value("仪器"));
    }

    @Test
    public void testGenerateItemQRCode() throws Exception {
        // 准备测试数据
        String qrCode = "A1INS-2024-001";

        // Mock Service层返回结果
        ResponseDTO<String> responseDTO = ResponseDTO.success(qrCode);
        when(qrCodeService.generateItemQRCode(anyLong())).thenReturn(responseDTO);

        // 执行测试
        mockMvc.perform(get("/api/v1/warehouse/qrcode/generate/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").value("A1INS-2024-001"));
    }
} 