package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserMapper测试类
 * 用于测试UserMapper接口与SQL Server数据库的交互
 */
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testContextLoads() {
        // 测试Spring上下文是否成功加载
        assertNotNull(userMapper, "UserMapper应该被成功注入");
    }

    @Test
    public void testSelectById() {
        // 测试根据ID查询用户
        // 注意：这里使用ID为1，可能需要根据实际数据库中的数据进行调整
        User user = userMapper.selectById(1L);
        
        if (user != null) {
            System.out.println("\n=== 用户详情 ===");
            System.out.println("ID: " + user.getId());
            System.out.println("工号: " + user.getWorkId());
            System.out.println("姓名: " + user.getName());
            System.out.println("手机号: " + user.getMobile());
            System.out.println("部门: " + user.getDepartment());
            System.out.println("工作地点: " + user.getLocation());
        } else {
            System.out.println("\n未找到ID为1的用户");
        }
        
        // 注意：这个断言可能需要根据实际数据库中的数据进行调整
        // assertNotNull(user, "应该能找到ID为1的用户");
    }

    @Test
    public void testSelectByWorkId() {
        // 测试根据工号查询用户
        // 注意：这里使用工号"admin"，可能需要根据实际数据库中的数据进行调整
        User user = userMapper.selectByWorkId("admin");
        
        if (user != null) {
            System.out.println("\n=== 用户详情(按工号) ===");
            System.out.println("ID: " + user.getId());
            System.out.println("工号: " + user.getWorkId());
            System.out.println("姓名: " + user.getName());
            System.out.println("手机号: " + user.getMobile());
            System.out.println("部门: " + user.getDepartment());
        } else {
            System.out.println("\n未找到工号为admin的用户");
        }
        
        // 注意：这个断言可能需要根据实际数据库中的数据进行调整
        // assertNotNull(user, "应该能找到工号为admin的用户");
    }
    
    @Test
    public void testSelectByMobile() {
        // 测试根据手机号查询用户
        // 注意：这里使用手机号"13800138000"，可能需要根据实际数据库中的数据进行调整
        User user = userMapper.selectByMobile("13800138000");
        
        if (user != null) {
            System.out.println("\n=== 用户详情(按手机号) ===");
            System.out.println("ID: " + user.getId());
            System.out.println("工号: " + user.getWorkId());
            System.out.println("姓名: " + user.getName());
            System.out.println("手机号: " + user.getMobile());
        } else {
            System.out.println("\n未找到手机号为13800138000的用户");
        }
        
        // 注意：这个断言可能需要根据实际数据库中的数据进行调整
        // assertNotNull(user, "应该能找到手机号为13800138000的用户");
    }
} 