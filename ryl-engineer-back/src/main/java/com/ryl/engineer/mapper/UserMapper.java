package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据工号查询用户
     *
     * @param workId 工号
     * @return 用户信息
     */
    User selectByWorkId(@Param("workId") String workId);
    
    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    User selectByMobile(@Param("mobile") String mobile);
    
    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);
    
    /**
     * 修改用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int update(User user);
    
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User selectById(@Param("id") Long id);
    
    /**
     * 更新用户最后登录时间
     *
     * @param id 用户ID
     * @return 影响行数
     */
    int updateLastLoginTime(@Param("id") Long id);
    
    /**
     * 更新用户令牌信息
     *
     * @param id 用户ID
     * @param token 令牌
     * @param tokenExpire 令牌过期时间
     * @return 影响行数
     */
    int updateToken(@Param("id") Long id, @Param("token") String token, @Param("tokenExpire") Date tokenExpire);
    
    /**
     * 查询工程师列表
     *
     * @param params 查询参数
     * @return 工程师列表
     */
    List<User> selectEngineers(@Param("params") Map<String, Object> params);
    
    /**
     * 查询销售人员列表
     *
     * @param params 查询参数
     * @return 销售人员列表
     */
    List<User> selectSales(@Param("params") Map<String, Object> params);
} 