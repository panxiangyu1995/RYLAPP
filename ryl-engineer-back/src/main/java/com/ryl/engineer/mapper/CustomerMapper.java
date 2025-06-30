package com.ryl.engineer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 客户Mapper接口
 */
@Mapper
public interface CustomerMapper {
    
    /**
     * 查询所有客户
     * @return 客户列表
     */
    List<Map<String, Object>> selectAllCustomers();
    
    /**
     * 分页查询所有客户
     * @param params 查询参数
     * @return 客户列表
     */
    List<Map<String, Object>> selectAllCustomersPage(@Param("params") Map<String, Object> params);
    
    /**
     * 根据销售经理ID查询客户列表
     * @param salesId 销售经理ID
     * @param params 查询参数
     * @return 客户列表
     */
    List<Map<String, Object>> selectCustomersBySalesId(
        @Param("salesId") Long salesId,
        @Param("params") Map<String, Object> params
    );
    
    /**
     * 根据客户ID列表查询客户列表
     * @param customerIds 客户ID列表
     * @param params 查询参数
     * @return 客户列表
     */
    List<Map<String, Object>> selectCustomersByIds(
        @Param("customerIds") List<Long> customerIds,
        @Param("params") Map<String, Object> params
    );
    
    /**
     * 根据ID查询客户
     * @param id 客户ID
     * @return 客户信息
     */
    Map<String, Object> selectCustomerById(Long id);
    
    /**
     * 插入客户
     * @param customer 客户信息
     * @return 影响行数
     */
    int insertCustomer(Map<String, Object> customer);
    
    /**
     * 更新客户信息
     * @param customer 客户信息
     * @return 影响行数
     */
    int updateCustomer(Map<String, Object> customer);
    
    /**
     * 删除客户
     * @param id 客户ID
     * @return 影响行数
     */
    int deleteCustomer(@Param("id") Long id);
    
    /**
     * 根据客户ID查询设备列表
     * @param customerId 客户ID
     * @return 设备列表
     */
    List<Map<String, Object>> selectCustomerDevices(@Param("customerId") Long customerId);
    
    /**
     * 统计所有客户总数
     * @param params 查询参数
     * @return 客户总数
     */
    int countAllCustomers(@Param("params") Map<String, Object> params);
    
    /**
     * 根据销售经理ID统计客户总数
     * @param salesId 销售经理ID
     * @param params 查询参数
     * @return 客户总数
     */
    int countCustomersBySalesId(
        @Param("salesId") Long salesId, 
        @Param("params") Map<String, Object> params
    );
    
    /**
     * 根据客户ID列表统计客户总数
     * @param customerIds 客户ID列表
     * @param params 查询参数
     * @return 客户总数
     */
    int countCustomersByIds(
        @Param("customerIds") List<Long> customerIds, 
        @Param("params") Map<String, Object> params
    );
} 