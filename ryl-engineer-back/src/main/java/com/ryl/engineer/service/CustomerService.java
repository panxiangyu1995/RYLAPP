package com.ryl.engineer.service;

import com.ryl.engineer.common.PageResult;
import java.util.List;
import java.util.Map;

/**
 * 客户服务接口
 */
public interface CustomerService {
    
    /**
     * 获取客户列表
     * @return 客户列表
     */
    List<Map<String, Object>> getCustomersList();
    
    /**
     * 获取所有客户列表（分页）
     * @param userId 当前用户ID
     * @param params 查询参数
     * @return 客户分页列表
     */
    PageResult<Map<String, Object>> getAllCustomersList(Long userId, Map<String, Object> params);
    
    /**
     * 获取销售经理负责的客户列表（分页）
     * @param userId 当前用户ID（销售经理ID）
     * @param params 查询参数
     * @return 客户分页列表
     */
    PageResult<Map<String, Object>> getSalesCustomersList(Long userId, Map<String, Object> params);
    
    /**
     * 获取工程师相关的客户列表（分页）
     * @param userId 当前用户ID（工程师ID）
     * @param params 查询参数
     * @return 客户分页列表
     */
    PageResult<Map<String, Object>> getEngineerCustomersList(Long userId, Map<String, Object> params);
    
    /**
     * 创建客户
     * @param customer 客户信息
     * @return 创建后的客户信息
     */
    Map<String, Object> createCustomer(Map<String, Object> customer);
    
    /**
     * 获取客户详情
     * @param customerId 客户ID
     * @return 客户详情
     */
    Map<String, Object> getCustomerDetail(Long customerId);
    
    /**
     * 获取客户设备列表
     * @param customerId 客户ID
     * @return 设备列表
     */
    List<Map<String, Object>> getCustomerDevices(Long customerId);
    
    /**
     * 带密码确认的删除客户
     * @param customerId 客户ID
     * @param userId 当前操作的用户ID
     * @param password 确认密码
     * @return 删除是否成功
     */
    boolean deleteCustomerWithConfirm(Long customerId, Long userId, String password);
} 