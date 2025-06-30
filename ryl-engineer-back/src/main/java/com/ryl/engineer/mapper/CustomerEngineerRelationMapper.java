package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.CustomerEngineerRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 工程师-客户关系Mapper接口
 */
@Mapper
public interface CustomerEngineerRelationMapper {
    
    /**
     * 添加工程师-客户关系
     * @param relation 关系实体
     * @return 影响行数
     */
    int insert(CustomerEngineerRelation relation);
    
    /**
     * 删除工程师-客户关系
     * @param id 关系ID
     * @return 影响行数
     */
    int delete(Long id);
    
    /**
     * 删除指定客户的所有工程师关系
     * @param customerId 客户ID
     * @return 影响行数
     */
    int deleteByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询工程师关联的客户ID列表
     * @param engineerId 工程师ID
     * @return 客户ID列表
     */
    List<Long> selectCustomerIdsByEngineerId(@Param("engineerId") Long engineerId);
    
    /**
     * 查询工程师关联的客户列表
     * @param engineerId 工程师ID
     * @param params 查询参数
     * @return 客户列表
     */
    List<Map<String, Object>> selectCustomersByEngineerId(
        @Param("engineerId") Long engineerId,
        @Param("params") Map<String, Object> params
    );
    
    /**
     * 查询与客户相关联的工程师ID列表
     * @param customerId 客户ID
     * @return 工程师ID列表
     */
    List<Long> selectEngineerIdsByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 检查工程师和客户之间是否存在关联
     * @param engineerId 工程师ID
     * @param customerId 客户ID
     * @return 关系ID，如无关系则返回null
     */
    Long checkRelationExists(
        @Param("engineerId") Long engineerId, 
        @Param("customerId") Long customerId
    );
} 