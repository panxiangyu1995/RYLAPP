package com.ryl.engineer.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.warehouse.entity.ItemRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 物品申请Mapper接口
 */
@Mapper
public interface ItemRequestMapper extends BaseMapper<ItemRequest> {

    /**
     * 获取用户的申请列表
     *
     * @param requesterId 申请人ID
     * @param requestType 申请类型（可选）
     * @param status      状态（可选）
     * @return 申请列表
     */
    @Select("<script>" +
            "SELECT * FROM item_request WHERE requester_id = #{requesterId} " +
            "<if test='requestType != null'> AND request_type = #{requestType} </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<ItemRequest> getByRequesterId(@Param("requesterId") Long requesterId,
                                       @Param("requestType") Integer requestType,
                                       @Param("status") Integer status);

    /**
     * 获取待审批的申请列表
     *
     * @param requestType 申请类型（可选）
     * @return 待审批申请列表
     */
    @Select("<script>" +
            "SELECT * FROM item_request WHERE status = 0 " +
            "<if test='requestType != null'> AND request_type = #{requestType} </if>" +
            "ORDER BY urgency DESC, create_time ASC" +
            "</script>")
    List<ItemRequest> getPendingRequests(@Param("requestType") Integer requestType);

    /**
     * 获取任务相关的申请列表
     *
     * @param taskId 任务ID
     * @return 申请列表
     */
    @Select("SELECT * FROM item_request WHERE task_id = #{taskId} ORDER BY create_time DESC")
    List<ItemRequest> getByTaskId(@Param("taskId") String taskId);
} 