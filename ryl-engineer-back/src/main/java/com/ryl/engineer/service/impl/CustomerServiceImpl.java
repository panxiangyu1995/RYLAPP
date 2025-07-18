package com.ryl.engineer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.CustomerEngineerRelationMapper;
import com.ryl.engineer.mapper.CustomerMapper;
import com.ryl.engineer.service.CustomerService;
import com.ryl.engineer.service.UserService;
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
    public Map<String, Object> createCustomer(Map<String, Object> customer) {
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
        try {
            // 验证密码
            User user = userService.getUserById(userId);
            if (user == null) {
                logger.error("用户不存在: {}", userId);
                return false;
            }
            
            // 获取存储的密码哈希
            String storedPasswordHash = user.getPassword();
            
            // 验证密码是否正确
            if (!passwordEncoder.matches(password, storedPasswordHash)) {
                logger.error("密码不正确，用户: {}", userId);
                return false;
            }
            
            // 验证客户是否存在
            Map<String, Object> customer = customerMapper.selectCustomerById(customerId);
            if (customer == null) {
                logger.error("客户不存在: {}", customerId);
                return false;
            }
            
            // 验证权限（作为额外安全措施）
            // 只有客户关联的销售和系统管理员可以删除客户
            // 系统管理员权限检查省略，实际项目中应当通过userService进行角色检查
            
            // 删除客户与工程师的关联关系
            customerEngineerRelationMapper.deleteByCustomerId(customerId);
            
            // 执行删除操作
            int result = customerMapper.deleteCustomer(customerId);
            return result > 0;
        } catch (Exception e) {
            logger.error("删除客户时发生错误", e);
            throw e;
        }
    }
} 