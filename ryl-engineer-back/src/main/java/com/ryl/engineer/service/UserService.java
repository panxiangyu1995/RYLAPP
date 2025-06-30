package com.ryl.engineer.service;

import com.ryl.engineer.dto.ForgotPasswordResponse;
import com.ryl.engineer.dto.LoginRequest;
import com.ryl.engineer.dto.LoginResponse;
import com.ryl.engineer.dto.RegisterRequest;
import com.ryl.engineer.dto.RegisterResponse;
import com.ryl.engineer.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应，登录失败时返回null
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册响应，注册失败时返回null或registered=false
     */
    RegisterResponse register(RegisterRequest request);
    
    /**
     * 忘记密码
     *
     * @param workId 工号
     * @param mobile 手机号
     * @return 忘记密码响应
     */
    ForgotPasswordResponse forgotPassword(String workId, String mobile);
    
    /**
     * 重置密码
     *
     * @param workId  工号
     * @param newPassword 新密码
     * @return 是否重置成功
     */
    boolean resetPassword(String workId, String newPassword);
    
    /**
     * 根据工号查询用户
     *
     * @param workId 工号
     * @return 用户信息
     */
    User getUserByWorkId(String workId);
    
    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    User getUserByMobile(String mobile);
    
    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 是否更新成功
     */
    boolean updateUser(User user);
    
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);
    
    /**
     * 获取工程师列表
     * 
     * @param params 查询参数
     * @return 工程师列表
     */
    List<Map<String, Object>> getEngineersList(Map<String, Object> params);
    
    /**
     * 获取销售人员列表
     * 
     * @param params 查询参数
     * @return 销售人员列表
     */
    List<Map<String, Object>> getSalesList(Map<String, Object> params);
} 