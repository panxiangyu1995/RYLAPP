package com.ryl.engineer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryl.engineer.common.Result;
import com.ryl.engineer.common.dto.PageDTO;
import com.ryl.engineer.common.dto.ResponseDTO;
import com.ryl.engineer.warehouse.dto.WarehouseItemDTO;
import com.ryl.engineer.warehouse.dto.request.DeleteRequest;
import com.ryl.engineer.warehouse.dto.request.WarehouseItemAddRequest;
import com.ryl.engineer.warehouse.dto.request.WarehouseItemQueryRequest;
import com.ryl.engineer.warehouse.dto.request.WarehouseItemUpdateRequest;
import com.ryl.engineer.warehouse.service.WarehouseItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WarehouseItemController单元测试
 */
public class WarehouseItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WarehouseItemService warehouseItemService;

    @InjectMocks
    private WarehouseItemController warehouseItemController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(warehouseItemController).build();
    }

    @Test
    public void testAddItem() throws Exception {
        // 准备测试数据
        WarehouseItemAddRequest request = new WarehouseItemAddRequest();
        request.setName("测试物品");
        request.setWarehouseId(1L);
        request.setCategoryId(1L);
        request.setQuantity(10);
        request.setCost(new BigDecimal("100.00"));

        // Mock Service层返回结果
        ResponseDTO<Void> responseDTO = ResponseDTO.success(null);
        when(warehouseItemService.addItem(any(WarehouseItemAddRequest.class))).thenReturn(responseDTO);

        // 执行测试
        mockMvc.perform(post("/api/v1/warehouse/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    public void testUpdateItem() throws Exception {
        // 准备测试数据
        WarehouseItemUpdateRequest request = new WarehouseItemUpdateRequest();
        request.setId(1L);
        request.setName("更新后的物品");
        request.setQuantity(20);
        request.setCost(new BigDecimal("200.00"));

        // Mock Service层返回结果
        ResponseDTO<Void> responseDTO = ResponseDTO.success(null);
        when(warehouseItemService.updateItem(any(WarehouseItemUpdateRequest.class))).thenReturn(responseDTO);

        // 执行测试
        mockMvc.perform(put("/api/v1/warehouse/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    public void testDeleteItem() throws Exception {
        // 准备测试数据
        DeleteRequest request = new DeleteRequest();
        request.setId(1L);
        request.setPassword("admin123");
        
        // Mock Service层返回结果
        ResponseDTO<Void> responseDTO = ResponseDTO.success(null);
        when(warehouseItemService.deleteItem(any(DeleteRequest.class))).thenReturn(responseDTO);

        // 执行测试
        mockMvc.perform(delete("/api/v1/warehouse/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    public void testGetItemById() throws Exception {
        // 准备测试数据
        WarehouseItemDTO itemDTO = new WarehouseItemDTO();
        itemDTO.setId(1L);
        itemDTO.setName("测试物品");
        itemDTO.setQuantity(10);
        itemDTO.setCost(new BigDecimal("100.00"));

        // Mock Service层返回结果
        ResponseDTO<WarehouseItemDTO> responseDTO = ResponseDTO.success(itemDTO);
        // 由于没有getItemById方法，我们模拟了使用ID作为参数的查询请求
        when(warehouseItemService.getItemList(eq(null), eq(null), eq("1"), eq(1), eq(10))).thenReturn(ResponseDTO.success(new PageDTO<WarehouseItemDTO>()));

        // 执行测试
        mockMvc.perform(get("/api/v1/warehouse/items/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetItemList() throws Exception {
        // 准备测试数据
        WarehouseItemQueryRequest request = new WarehouseItemQueryRequest();
        request.setWarehouseId(1L);
        request.setCategoryId(1L);
        request.setPageNum(1);
        request.setPageSize(10);

        List<WarehouseItemDTO> itemList = new ArrayList<>();
        WarehouseItemDTO item1 = new WarehouseItemDTO();
        item1.setId(1L);
        item1.setName("物品1");
        itemList.add(item1);

        WarehouseItemDTO item2 = new WarehouseItemDTO();
        item2.setId(2L);
        item2.setName("物品2");
        itemList.add(item2);

        PageDTO<WarehouseItemDTO> pageDTO = new PageDTO<>();
        pageDTO.setList(itemList);
        pageDTO.setTotal(2L);
        pageDTO.setPages(1);
        pageDTO.setCurrent(1);
        pageDTO.setSize(10);

        // Mock Service层返回结果
        ResponseDTO<PageDTO<WarehouseItemDTO>> responseDTO = ResponseDTO.success(pageDTO);
        when(warehouseItemService.getItemList(
                eq(request.getWarehouseId()), 
                eq(request.getCategoryId()), 
                eq(request.getName()), 
                eq(request.getPageNum()), 
                eq(request.getPageSize())
        )).thenReturn(responseDTO);

        // 执行测试
        mockMvc.perform(post("/api/v1/warehouse/items/list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }
} 