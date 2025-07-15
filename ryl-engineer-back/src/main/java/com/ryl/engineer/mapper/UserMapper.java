package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.vo.EngineerSimpleVO;
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
     * 插入用户角色关联记录
     *
     * @param userId 用户ID
     * @param roleCode 角色ID
     * @return 影响行数
     */
    int insertUserRole(@Param("userId") Long userId, @Param("roleCode") String roleCode);

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

    /**
     * 根据技术分类查找工程师
     * @param category 技术分类
     * @return 用户列表
     */
    List<User> findEngineersByTechnicalCategory(@Param("category") String category);

    /**
     * 根据角色代码查找用户
     * @param roleCode 角色代码
     * @return 用户列表
     */
    List<User> findUsersByRole(@Param("roleCode") String roleCode);

    /**
     * 根据角色编码查找第一个用户
     * @param roleCode 角色编码
     * @return 用户
     */
    User findFirstByRole(@Param("roleCode") String roleCode);

    /**
     * 根据任务ID列表批量查询关联的工程师简要信息
     *
     * @param taskIds 任务ID列表
     * @return 工程师简要信息列表
     */
    List<EngineerSimpleVO> findEngineersByTaskIds(@Param("list") List<String> taskIds);
} 