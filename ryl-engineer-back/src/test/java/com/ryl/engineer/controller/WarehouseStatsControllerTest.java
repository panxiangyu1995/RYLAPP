package com.ryl.engineer.controller;

import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.StockStatsDTO;
import com.ryl.engineer.warehouse.service.StockRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * WarehouseStatsController单元测试
 */
public class WarehouseStatsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StockRecordService stockRecordService;

    @InjectMocks
    private WarehouseStatsController warehouseStatsController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(warehouseStatsController).build();
    }

    @Test
    public void testGetStockStats() throws Exception {
        // 准备测试数据
        StockStatsDTO statsDTO = new StockStatsDTO();
        
        // 入库统计
        List<StockStatsDTO.StockDateStatsDTO> inStats = new ArrayList<>();
        StockStatsDTO.StockDateStatsDTO inStat = new StockStatsDTO.StockDateStatsDTO();
        inStat.setDate("2024-01-01 - 2024-01-31");
        inStat.setCount(10);
        inStat.setQuantity(100);
        inStat.setValue(new BigDecimal("10000.00"));
        inStats.add(inStat);
        statsDTO.setInStats(inStats);
        
        // 出库统计
        List<StockStatsDTO.StockDateStatsDTO> outStats = new ArrayList<>();
        StockStatsDTO.StockDateStatsDTO outStat = new StockStatsDTO.StockDateStatsDTO();
        outStat.setDate("2024-01-01 - 2024-01-31");
        outStat.setCount(5);
        outStat.setQuantity(50);
        outStat.setValue(new BigDecimal("5000.00"));
        outStats.add(outStat);
        statsDTO.setOutStats(outStats);
        
        // 分类统计
        List<StockStatsDTO.CategoryStatsDTO> categoryStats = new ArrayList<>();
        StockStatsDTO.CategoryStatsDTO categoryStat = new StockStatsDTO.CategoryStatsDTO();
        categoryStat.setCategoryId(1L);
        categoryStat.setInCount(10);
        categoryStat.setInQuantity(100);
        categoryStat.setInValue(new BigDecimal("10000.00"));
        categoryStat.setOutCount(5);
        categoryStat.setOutQuantity(50);
        categoryStat.setOutValue(new BigDecimal("5000.00"));
        categoryStats.add(categoryStat);
        statsDTO.setCategoryStats(categoryStats);

        // Mock Service层返回结果
        ResponseDTO<StockStatsDTO> responseDTO = ResponseDTO.success(statsDTO);
        when(stockRecordService.getStockStats(anyLong(), any(LocalDate.class), any(LocalDate.class))).thenReturn(responseDTO);

        // 执行测试
        mockMvc.perform(get("/api/v1/warehouse/stats/stock")
                .param("warehouseId", "1")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.inStats[0].count").value(10))
                .andExpect(jsonPath("$.data.outStats[0].count").value(5))
                .andExpect(jsonPath("$.data.categoryStats[0].categoryId").value(1));
    }

    @Test
    public void testGetInventoryValue() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/v1/warehouse/stats/value")
                .param("warehouseId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    public void testGetCategoryStats() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/v1/warehouse/stats/category")
                .param("warehouseId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }
} 