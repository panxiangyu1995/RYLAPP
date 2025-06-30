package com.ryl.engineer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryl.engineer.common.dto.PageDTO;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.StockRecordDTO;
import com.ryl.engineer.warehouse.dto.request.StockInRequest;
import com.ryl.engineer.warehouse.dto.request.StockOutRequest;
import com.ryl.engineer.warehouse.service.StockRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * StockRecordController单元测试
 */
public class StockRecordControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StockRecordService stockRecordService;

    @InjectMocks
    private StockRecordController stockRecordController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(stockRecordController).build();
    }

    @Test
    public void testStockIn() throws Exception {
        // 准备测试数据
        StockInRequest request = new StockInRequest();
        request.setItemId(1L);
        request.setQuantity(10);
        request.setOperationTime(LocalDateTime.now());
        request.setDescription("测试入库");

        // Mock Service层返回结果
        ResponseDTO<Void> responseDTO = ResponseDTO.success(null);
        when(stockRecordService.stockIn(any(StockInRequest.class))).thenReturn(responseDTO);

        // 执行测试
        mockMvc.perform(post("/api/v1/warehouse/stock/in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    public void testStockOut() throws Exception {
        // 准备测试数据
        StockOutRequest request = new StockOutRequest();
        request.setItemId(1L);
        request.setQuantity(5);
        request.setOperationTime(LocalDateTime.now());
        request.setDescription("测试出库");

        // Mock Service层返回结果
        ResponseDTO<Void> responseDTO = ResponseDTO.success(null);
        when(stockRecordService.stockOut(any(StockOutRequest.class))).thenReturn(responseDTO);

        // 执行测试
        mockMvc.perform(post("/api/v1/warehouse/stock/out")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    public void testGetRecords() throws Exception {
        // 准备测试数据
        List<StockRecordDTO> recordList = new ArrayList<>();
        StockRecordDTO record1 = new StockRecordDTO();
        record1.setId(1L);
        record1.setItemId(1L);
        record1.setItemName("测试物品");
        record1.setRecordType(1);
        record1.setQuantity(10);
        recordList.add(record1);

        PageDTO<StockRecordDTO> pageDTO = new PageDTO<>();
        pageDTO.setList(recordList);
        pageDTO.setTotal(1L);
        pageDTO.setPages(1);
        pageDTO.setCurrent(1);
        pageDTO.setSize(10);

        // Mock Service层返回结果
        ResponseDTO<PageDTO<StockRecordDTO>> responseDTO = ResponseDTO.success(pageDTO);
        when(stockRecordService.getRecords(anyLong(), anyInt(), anyLong(), any(), any(), anyInt(), anyInt())).thenReturn(responseDTO);

        // 执行测试
        mockMvc.perform(get("/api/v1/warehouse/stock/records")
                .param("itemId", "1")
                .param("recordType", "1")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].itemName").value("测试物品"));
    }
} 