package com.ryl.engineer.service.impl;

import com.ryl.engineer.common.exception.PermissionDeniedException;
import com.ryl.engineer.mapper.CustomerMapper;
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.engineer.service.PermissionService;
import com.ryl.engineer.utils.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public void checkWarehouseDeletePermission() {
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            throw new PermissionDeniedException("用户未登录或凭证无效。");
        }
        List<String> roles = userMapper.selectRolesByUserId(userId);

        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_WAREHOUSE")) {
            return; // 权限通过
        }
        throw new PermissionDeniedException("无权删除仓库物品，需要系统或仓库管理员权限。");
    }

    @Override
    public void checkCustomerDeletePermission(Long customerId) {
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            throw new PermissionDeniedException("用户未登录或凭证无效。");
        }
        List<String> roles = userMapper.selectRolesByUserId(userId);

        if (roles.contains("ROLE_ADMIN")) {
            return; // 管理员直接通过
        }

        if (roles.contains("ROLE_SALES")) {
            Map<String, Object> customer = customerMapper.selectCustomerById(customerId);
            if (customer == null) {
                // 在权限检查层面，如果资源不存在，可以视为无权限
                throw new PermissionDeniedException("无权删除此客户或客户不存在。");
            }
            // 假设数据库中存储负责人ID的字段是 sales_id
            Object salesIdObj = customer.get("sales_id");
            if (salesIdObj != null && userId.equals(Long.valueOf(salesIdObj.toString()))) {
                return; // 是自己的客户，通过
            }
        }
        throw new PermissionDeniedException("无权删除此客户。");
    }
} 