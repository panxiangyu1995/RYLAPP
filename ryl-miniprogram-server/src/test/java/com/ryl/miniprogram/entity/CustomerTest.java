package com.ryl.miniprogram.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 客户实体类测试
 */
public class CustomerTest {
    
    @Test
    public void testCustomerFields() {
        // 创建客户对象
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("测试客户");
        customer.setContact("张三");
        customer.setPhone("13800138000");
        customer.setEmail("test@example.com");
        customer.setAddress("北京市海淀区");
        customer.setRemark("测试备注");
        customer.setOpenid("wx123456");
        customer.setUnionid("unionid123");
        customer.setNickname("小张");
        customer.setAvatarUrl("https://example.com/avatar.jpg");
        customer.setGender(1);
        customer.setCountry("中国");
        customer.setProvince("北京");
        customer.setCity("北京");
        customer.setLanguage("zh_CN");
        customer.setSessionKey("session123");
        
        // 验证字段值
        assertEquals(1L, customer.getId());
        assertEquals("测试客户", customer.getName());
        assertEquals("张三", customer.getContact());
        assertEquals("13800138000", customer.getPhone());
        assertEquals("test@example.com", customer.getEmail());
        assertEquals("北京市海淀区", customer.getAddress());
        assertEquals("测试备注", customer.getRemark());
        assertEquals("wx123456", customer.getOpenid());
        assertEquals("unionid123", customer.getUnionid());
        assertEquals("小张", customer.getNickname());
        assertEquals("https://example.com/avatar.jpg", customer.getAvatarUrl());
        assertEquals(1, customer.getGender());
        assertEquals("中国", customer.getCountry());
        assertEquals("北京", customer.getProvince());
        assertEquals("北京", customer.getCity());
        assertEquals("zh_CN", customer.getLanguage());
        assertEquals("session123", customer.getSessionKey());
    }
}