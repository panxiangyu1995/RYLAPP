package com.ryl.engineer.controller;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.common.Result;
import com.ryl.engineer.service.CustomerService;
import com.ryl.engineer.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户控制器
 */
@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    
    @Autowired
    private CustomerService customerService;

    /**
     * 获取客户列表（基础接口，根据参数获取特定类型的客户列表）
     * @param params 查询参数，包含path（可选）、keyword（可选）、page（默认1）、size（默认20）
     * @return 客户分页列表
     */
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> getCustomersList(@RequestParam(required = false) Map<String, Object> params) {
        try {
            Long userId = UserContextHolder.getUserId();
            String path = params.get("path") != null ? params.get("path").toString() : null;
            
            // 根据路径调用不同的服务方法
            PageResult<Map<String, Object>> customers;
            if ("/list/sales".equals(path)) {
                customers = customerService.getSalesCustomersList(userId, params);
            } else if ("/list/engineer".equals(path)) {
                customers = customerService.getEngineerCustomersList(userId, params);
            } else {
                customers = customerService.getAllCustomersList(userId, params);
            }
            return Result.success(customers);
        } catch (Exception e) {
            logger.error("获取客户列表失败", e);
            return Result.error("获取客户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有客户列表（管理员角色）
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @param keyword 搜索关键词（可选）
     * @return 客户分页列表
     */
    @GetMapping("/list/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<Map<String, Object>>> getAllCustomersList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword) {
        try {
            Long userId = UserContextHolder.getUserId();
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("size", size);
            if (keyword != null) {
                params.put("keyword", keyword);
            }
            PageResult<Map<String, Object>> customers = customerService.getAllCustomersList(userId, params);
            return Result.success(customers);
        } catch (Exception e) {
            logger.error("获取所有客户列表失败", e);
            return Result.error("获取所有客户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取销售经理负责的客户列表
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @param keyword 搜索关键词（可选）
     * @return 客户分页列表
     */
    @GetMapping("/list/sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public Result<PageResult<Map<String, Object>>> getSalesCustomersList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword) {
        try {
            Long userId = UserContextHolder.getUserId();
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("size", size);
            if (keyword != null) {
                params.put("keyword", keyword);
            }
            PageResult<Map<String, Object>> customers = customerService.getSalesCustomersList(userId, params);
            return Result.success(customers);
        } catch (Exception e) {
            logger.error("获取销售经理客户列表失败", e);
            return Result.error("获取销售经理客户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取工程师相关的客户列表
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @param keyword 搜索关键词（可选）
     * @return 客户分页列表
     */
    @GetMapping("/list/engineer")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    public Result<PageResult<Map<String, Object>>> getEngineerCustomersList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword) {
        try {
            Long userId = UserContextHolder.getUserId();
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("size", size);
            if (keyword != null) {
                params.put("keyword", keyword);
            }
            PageResult<Map<String, Object>> customers = customerService.getEngineerCustomersList(userId, params);
            return Result.success(customers);
        } catch (Exception e) {
            logger.error("获取工程师客户列表失败", e);
            return Result.error("获取工程师客户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建新客户
     *
     * @param customer 客户信息
     * @return 创建结果
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public Result<Map<String, Object>> createCustomer(@RequestBody Map<String, Object> customer) {
        try {
            Map<String, Object> createdCustomer = customerService.createCustomer(customer);
            logger.info("创建客户成功: {}", createdCustomer);
            return Result.success(createdCustomer);
        } catch (Exception e) {
            logger.error("创建客户失败", e);
            return Result.error("创建客户失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取客户详情
     * @param customerId 客户ID
     * @return 客户详情
     */
    @GetMapping("/{customerId}")
    public Result<Map<String, Object>> getCustomerDetail(@PathVariable("customerId") Long customerId) {
        try {
            Map<String, Object> customer = customerService.getCustomerDetail(customerId);
            if (customer == null) {
                return Result.error(404, "客户不存在");
            }
            return Result.success(customer);
        } catch (Exception e) {
            logger.error("获取客户详情失败", e);
            return Result.error("获取客户详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取客户设备列表
     * @param customerId 客户ID
     * @return 设备列表
     */
    @GetMapping("/{customerId}/devices")
    public Result<List<Map<String, Object>>> getCustomerDevices(@PathVariable("customerId") Long customerId) {
        try {
            List<Map<String, Object>> devices = customerService.getCustomerDevices(customerId);
            return Result.success(devices);
        } catch (Exception e) {
            logger.error("获取客户设备列表失败", e);
            return Result.error("获取客户设备列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 带密码确认的删除客户
     * @param params 参数，包含客户ID和确认密码
     * @return 删除结果
     */
    @PostMapping("/delete-with-confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public Result<Boolean> deleteCustomerWithConfirm(@RequestBody Map<String, Object> params) {
        try {
            Long customerId = Long.valueOf(params.get("id").toString());
            String password = (String) params.get("password");
            Long userId = UserContextHolder.getUserId();
            
            boolean result = customerService.deleteCustomerWithConfirm(customerId, userId, password);
            if (result) {
                logger.info("客户删除成功，ID: {}", customerId);
                return Result.success(true);
            } else {
                return Result.error("删除失败，请检查密码是否正确");
            }
        } catch (Exception e) {
            logger.error("删除客户失败", e);
            return Result.error("删除客户失败: " + e.getMessage());
        }
    }
} 