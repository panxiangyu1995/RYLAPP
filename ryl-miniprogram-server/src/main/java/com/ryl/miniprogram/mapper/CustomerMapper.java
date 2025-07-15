package com.ryl.miniprogram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.miniprogram.entity.Customer;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 客户Mapper接口
 */
@Repository
public interface CustomerMapper extends BaseMapper<Customer> {
    
    /**
     * 根据openid查询客户
     */
    Customer selectByOpenid(@Param("openid") String openid);
    
    /**
     * 根据联系人名称（用户名）查询客户
     */
    Customer selectByContact(@Param("contact") String contact);
    
    /**
     * 更新客户微信信息
     */
    int updateWechatInfo(Customer customer);
    
    /**
     * 更新登录信息
     */
    int updateLoginInfo(Customer customer);
}