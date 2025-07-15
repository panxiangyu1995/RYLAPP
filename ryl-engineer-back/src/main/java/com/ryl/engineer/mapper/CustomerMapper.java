package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     * @param page 分页对象
     * @param params 查询参数
     * @return 客户列表
     */
    IPage<Map<String, Object>> selectAllCustomersPage(Page<Map<String, Object>> page, @Param("params") Map<String, Object> params);

    /**
     * 根据销售经理ID查询客户列表
     * @param page 分页对象
     * @param salesId 销售经理ID
     * @param params 查询参数
     * @return 客户列表
     */
    IPage<Map<String, Object>> selectCustomersBySalesId(
            Page<Map<String, Object>> page,
            @Param("salesId") Long salesId,
            @Param("params") Map<String, Object> params
    );

    /**
     * 根据客户ID列表查询客户列表
     * @param page 分页对象
     * @param customerIds 客户ID列表
     * @param params 查询参数
     * @return 客户列表
     */
    IPage<Map<String, Object>> selectCustomersByIds(
            Page<Map<String, Object>> page,
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

} 