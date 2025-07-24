package com.ryl.engineer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.CustomerEngineerRelationMapper;
import com.ryl.engineer.mapper.CustomerMapper;
import com.ryl.engineer.service.CustomerService;
import com.ryl.engineer.service.UserService;
import com.ryl.engineer.service.PermissionService;
import com.ryl.engineer.utils.CustomPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ryl.engineer.common.exception.PermissionDeniedException;

/**
 * 客户服务实现类
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private CustomPasswordEncoder passwordEncoder;
    
    @Autowired
    private CustomerEngineerRelationMapper customerEngineerRelationMapper;
    
    @Override
    public List<Map<String, Object>> getCustomersList() {
        return customerMapper.selectAllCustomers();
    }
    
    @Override
    public PageResult<Map<String, Object>> getAllCustomersList(Long userId, Map<String, Object> params) {
        // 获取分页参数
        Integer pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        Integer pageSize = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 20;

        Page<Map<String, Object>> page = new Page<>(pageNum, pageSize);
        params.put("orderBy", "c.create_time DESC");

        IPage<Map<String, Object>> pageResult = customerMapper.selectAllCustomersPage(page, params);

        return PageResult.fromPage(pageResult);
    }
    
    @Override
    public PageResult<Map<String, Object>> getSalesCustomersList(Long userId, Map<String, Object> params) {
        // 获取分页参数
        Integer pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        Integer pageSize = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 20;

        Page<Map<String, Object>> page = new Page<>(pageNum, pageSize);
        params.put("orderBy", "c.create_time DESC");

        IPage<Map<String, Object>> pageResult = customerMapper.selectCustomersBySalesId(page, userId, params);

        return PageResult.fromPage(pageResult);
    }
    
    @Override
    public PageResult<Map<String, Object>> getEngineerCustomersList(Long userId, Map<String, Object> params) {
        // 获取分页参数
        Integer pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        Integer pageSize = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 20;

        // 获取工程师关联的客户ID列表
        List<Long> customerIds = customerEngineerRelationMapper.selectCustomerIdsByEngineerId(userId);

        // 如果没有关联客户，直接返回空列表
        if (customerIds.isEmpty()) {
            return PageResult.fromPage(new Page<>(pageNum, pageSize));
        }

        Page<Map<String, Object>> page = new Page<>(pageNum, pageSize);
        params.put("orderBy", "c.create_time DESC");

        IPage<Map<String, Object>> pageResult = customerMapper.selectCustomersByIds(page, customerIds, params);

        return PageResult.fromPage(pageResult);
    }
    
    @Override
    public Map<String, Object> createCustomer(Map<String, Object> customer, Long creatorId) {
        // 后端强制设置创建者ID
        customer.put("sales_id", creatorId);
        customer.put("creator_id", creatorId); // 也可以增加一个通用的创建者ID
        customer.put("create_time", new java.util.Date());
        
        customerMapper.insertCustomer(customer);
        Long id = (Long) customer.get("id");
        return customerMapper.selectCustomerById(id);
    }
    
    @Override
    public Map<String, Object> getCustomerDetail(Long customerId) {
        return customerMapper.selectCustomerById(customerId);
    }
    
    @Override
    public List<Map<String, Object>> getCustomerDevices(Long customerId) {
        return customerMapper.selectCustomerDevices(customerId);
    }
    
    @Override
    @Transactional
    public boolean deleteCustomerWithConfirm(Long customerId, Long userId, String password) {
        // 1. 验证安全密码
            User user = userService.getUserById(userId);
            if (user == null) {
            throw new RuntimeException("操作用户不存在");
        }
        if (!userService.verifySecurityPassword(user.getWorkId(), password)) {
            throw new PermissionDeniedException("安全密码错误。");
        }

        // 2. 验证操作权限
        permissionService.checkCustomerDeletePermission(customerId);
        
        // 3. 执行删除业务逻辑
        try {
            // 验证客户是否存在 (虽然权限层已检查，但业务层再检查一次更健壮)
            Map<String, Object> customer = customerMapper.selectCustomerById(customerId);
            if (customer == null) {
                logger.error("客户不存在: {}", customerId);
                return false;
            }
            
            customerEngineerRelationMapper.deleteByCustomerId(customerId);
            int result = customerMapper.deleteCustomer(customerId);
            return result > 0;
        } catch (Exception e) {
            logger.error("删除客户时发生错误", e);
            throw e; // 抛出以便事务回滚
        }
    }
} 