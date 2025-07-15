package com.ryl.miniprogram.controller;

import com.ryl.miniprogram.dto.WxLoginDTO;
import com.ryl.miniprogram.entity.Customer;
import com.ryl.miniprogram.service.CustomerService;
import com.ryl.miniprogram.util.JwtTokenUtil;
import com.ryl.miniprogram.vo.ResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * WechatController单元测试
 */
public class WechatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private WechatController wechatController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(wechatController).build();
    }

    @Test
    public void testWxLogin() throws Exception {
        // 准备测试数据
        String openid = "test_openid";
        String sessionKey = "test_session_key";
        String token = "test_token";
        
        // 模拟登录结果
        Map<String, Object> loginResult = new HashMap<>();
        loginResult.put("token", token);
        loginResult.put("openid", openid);
        
        // 模拟Customer对象
        Customer customer = new Customer();
        customer.setOpenid(openid);
        customer.setId(1L);
        
        // 模拟service方法
        when(customerService.login(anyString())).thenReturn(loginResult);
        when(customerService.getByOpenid(anyString())).thenReturn(customer);
        when(customerService.updateUserInfo(any(Customer.class), any(Long.class))).thenReturn(true);
        
        // 创建WxLoginDTO对象
        WxLoginDTO wxLoginDTO = new WxLoginDTO();
        wxLoginDTO.setCode("test_code");
        
        // 执行测试
        ResultVO<?> response = wechatController.login(wxLoginDTO);
        
        // 验证结果
        assert response.getCode() == 200;
        assert response.getData() == loginResult;
    }
} 