package com.ryl.miniprogram.controller;

import com.ryl.miniprogram.dto.UserInfoDTO;
import com.ryl.miniprogram.entity.Customer;
import com.ryl.miniprogram.service.CustomerService;
import com.ryl.miniprogram.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户相关接口控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private CustomerService customerService;

    /**
     * 获取所有用户列表（联系人列表）
     */
    @GetMapping("/list")
    public ResultVO<?> getUserList() {
        List<Customer> list = customerService.listAll();
        return ResultVO.success(list);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public ResultVO<?> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Customer customer = customerService.getById(userId);
        return ResultVO.success(customer);
    }
    
    /**
     * 获取当前用户资料（供前端Profile页面使用）
     */
    @GetMapping("/profile")
    public ResultVO<?> getUserProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Customer customer = customerService.getById(userId);
        return ResultVO.success(customer);
    }
    
    /**
     * 更新用户资料（PUT方法，供前端Profile页面使用）
     */
    @PutMapping("/profile")
    public ResultVO<?> updateUserProfile(@RequestBody @Validated UserInfoDTO userInfoDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("更新用户资料，userId: {}, 请求数据: {}", userId, userInfoDTO);
        Customer customer = new Customer();
        BeanUtils.copyProperties(userInfoDTO, customer);
        boolean result = customerService.updateUserInfo(customer, userId);
        log.info("用户资料更新结果: {}", result ? "成功" : "失败");
        return result ? ResultVO.success(customer, "更新成功") : ResultVO.failed("更新失败");
    }
    
    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    public ResultVO<?> updateUserInfo(@RequestBody @Validated UserInfoDTO userInfoDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("通过POST更新用户资料，userId: {}, 请求数据: {}", userId, userInfoDTO);
        Customer customer = new Customer();
        BeanUtils.copyProperties(userInfoDTO, customer);
        boolean result = customerService.updateUserInfo(customer, userId);
        log.info("用户资料更新结果: {}", result ? "成功" : "失败");
        return result ? ResultVO.success(null, "更新成功") : ResultVO.failed("更新失败");
    }
} 