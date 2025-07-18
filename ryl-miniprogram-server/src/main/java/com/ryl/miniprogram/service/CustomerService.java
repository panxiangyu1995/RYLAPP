package com.ryl.miniprogram.service;

import com.ryl.miniprogram.entity.Customer;
import com.ryl.miniprogram.vo.PageVO;

import java.util.List;

/**
 * 客户服务接口
 */
public interface CustomerService {
    
    /**
     * 根据ID查询客户
     */
    Customer getById(Long id);
    
    /**
     * 根据openid查询客户
     */
    Customer getByOpenid(String openid);
    
    /**
     * 根据联系人名称（用户名）查询客户
     */
    Customer getByContact(String contact);
    
    /**
     * 保存客户信息
     */
    boolean save(Customer customer);
    
    /**
     * 更新客户信息
     */
    boolean update(Customer customer);
    
    /**
     * 更新客户微信信息
     */
    boolean updateWechatInfo(Customer customer);
    
    /**
     * 分页查询客户列表
     */
    PageVO<Customer> listByPage(int pageNum, int pageSize);
    
    /**
     * 查询所有客户列表
     */
    List<Customer> listAll();

    /**
     * 微信登录
     * @param code 登录凭证
     * @param nickname 用户昵称
     * @param avatarUrl 用户头像
     * @param phoneCode 手机号加密凭证
     * @return token及用户信息
     */
    Object login(String code, String nickname, String avatarUrl, String phoneCode);
    
    /**
     * 使用测试openid进行登录（开发/测试环境使用）
     * @param openid 测试用户的openid
     * @return 登录结果，包含token和用户信息
     */
    Object loginWithTestOpenid(String openid);
    
    /**
     * 账号密码登录
     * @param contact 联系人名称（用户名）
     * @param password 密码
     * @return 登录结果，包含token和用户信息
     */
    Object loginWithPassword(String contact, String password);
    
    /**
     * 注册新用户
     * @param customer 包含注册信息的客户对象
     * @return 注册结果，成功返回true
     */
    boolean register(Customer customer);
    
    /**
     * 检查联系人名称（用户名）是否已存在
     * @param contact 联系人名称（用户名）
     * @return 存在返回true，不存在返回false
     */
    boolean checkContactExists(String contact);
    
    /**
     * 更新用户信息
     * @param customer 用户信息
     * @param userId 用户ID
     * @return 是否更新成功
     */
    boolean updateUserInfo(Customer customer, Long userId);
    
    /**
     * 更新用户头像
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 是否更新成功
     */
    Customer updateAvatar(Long userId, String avatarUrl);
} 