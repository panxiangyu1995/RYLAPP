package com.ryl.engineer.service;

import com.ryl.engineer.dto.ForgotPasswordResponse;
import com.ryl.engineer.dto.LoginRequest;
import com.ryl.engineer.dto.LoginResponse;
import com.ryl.engineer.dto.RegisterRequest;
import com.ryl.engineer.dto.RegisterResponse;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.engineer.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试类
 */
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 测试前准备工作，确保admin用户存在
     */
    @BeforeEach
    public void setUp() {
        // 检查admin用户是否存在，如果不存在则创建
        User adminUser = userMapper.selectByWorkId("admin");
        if (adminUser == null) {
            User user = new User();
            user.setWorkId("admin");
            user.setName("系统管理员");
            user.setMobile("13800138000");
            // 加密密码"123456"
            user.setPassword(PasswordUtil.encode("123456"));
            user.setDepartment("管理部");
            user.setLocation("总部");
            user.setStatus(1);
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            userMapper.insert(user);
            System.out.println("创建了admin测试账号");
        } else {
            // 更新admin用户密码为"123456"
            adminUser.setPassword(PasswordUtil.encode("123456"));
            userMapper.update(adminUser);
            System.out.println("更新了admin测试账号密码");
        }
    }

    /**
     * 测试用户登录功能
     */
    @Test
    public void testLogin() {
        // 使用初始化数据中的admin用户测试
        LoginRequest request = new LoginRequest();
        request.setWorkId("admin");
        request.setPassword("123456"); // 明文密码，会在服务中验证加密后的密码
        
        // 添加调试信息
        User user = userMapper.selectByWorkId("admin");
        if (user == null) {
            System.out.println("错误：数据库中不存在admin用户，请先执行初始化SQL脚本");
        } else {
            System.out.println("找到用户: " + user.getName() + ", 工号: " + user.getWorkId());
            System.out.println("数据库中的密码: " + user.getPassword());
            System.out.println("密码验证结果: " + PasswordUtil.verify("123456", user.getPassword()));
        }
        
        LoginResponse response = userService.login(request);
        
        // 验证登录结果
        assertNotNull(response, "登录响应不应为空");
        assertEquals("admin", response.getWorkId(), "工号应匹配");
        assertNotNull(response.getToken(), "应该生成令牌");
        assertNotNull(response.getName(), "应该返回用户姓名");
        
        System.out.println("用户登录成功，令牌: " + response.getToken());
        
        // 测试错误的密码
        request.setPassword("wrong_password");
        LoginResponse errorResponse = userService.login(request);
        assertNull(errorResponse, "使用错误密码应该登录失败");
    }

    /**
     * 测试用户注册功能
     */
    @Test
    @Transactional
    public void testRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setWorkId("TEST002");
        request.setName("注册测试用户");
        request.setMobile("13999999999");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setDepartment("测试部门");
        request.setLocation("测试地点");
        
        RegisterResponse response = userService.register(request);
        assertNotNull(response, "注册响应不应为空");
        assertTrue(response.getRegistered(), "注册应该成功");
        assertEquals("TEST002", response.getWorkId(), "工号应该匹配");
        assertEquals("注册测试用户", response.getName(), "姓名应该匹配");
        
        // 验证用户是否已创建
        User user = userService.getUserByWorkId("TEST002");
        assertNotNull(user, "应该能够查询到新注册的用户");
        assertEquals("注册测试用户", user.getName(), "用户名应该匹配");
        assertEquals("13999999999", user.getMobile(), "手机号应该匹配");
        
        // 测试重复注册
        RegisterResponse duplicateResponse = userService.register(request);
        assertNull(duplicateResponse, "重复注册应该失败");
    }

    /**
     * 测试忘记密码功能
     */
    @Test
    public void testForgotPassword() {
        // 使用初始化数据中的admin用户测试
        ForgotPasswordResponse response = userService.forgotPassword("admin", "13800138000");
        
        assertTrue(response.getReset(), "使用正确的工号和手机号应该验证通过");
        assertEquals("admin", response.getWorkId(), "工号应该匹配");
        
        // 测试错误的手机号
        ForgotPasswordResponse errorResponse = userService.forgotPassword("admin", "13800138001");
        assertFalse(errorResponse.getReset(), "使用错误的手机号应该验证失败");
    }

    /**
     * 测试重置密码功能
     */
    @Test
    @Transactional
    public void testResetPassword() {
        // 重置admin用户的密码
        boolean result = userService.resetPassword("admin", "newpassword123");
        assertTrue(result, "重置密码应该成功");
        
        // 验证新密码是否生效
        LoginRequest request = new LoginRequest();
        request.setWorkId("admin");
        request.setPassword("newpassword123");
        
        LoginResponse response = userService.login(request);
        assertNotNull(response, "使用新密码应该能够登录");
        
        // 验证旧密码不再有效
        request.setPassword("123456");
        LoginResponse oldPasswordResponse = userService.login(request);
        assertNull(oldPasswordResponse, "使用旧密码应该登录失败");
    }
} 