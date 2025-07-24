package com.ryl.engineer.controller;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.common.Result;
import com.ryl.engineer.dto.contact.ContactDTO;
import com.ryl.engineer.dto.contact.ContactGroupDTO;
import com.ryl.engineer.service.ContactsService;
import com.ryl.engineer.utils.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 联系人控制器
 */
@RestController
@RequestMapping("/api/v1/contacts")
public class ContactsController {
    
    @Autowired
    private ContactsService contactsService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 获取联系人列表
     */
    @GetMapping("/list")
    public Result<PageResult<ContactDTO>> getContactsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer status) {
        
        Long userId = UserContextHolder.getUserId();
        PageResult<ContactDTO> result = contactsService.getContactsList(userId, page, size, keyword, department, status);
        return Result.success(result);
    }
    
    /**
     * 获取工程师状态列表（按工作地点分组）
     */
    @GetMapping("/engineers")
    public Result<?> getEngineerStatusList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        try {
            Map<String, List<Map<String, Object>>> result = contactsService.getEngineerStatusByLocation(status, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取工程师状态列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取工程师详情及任务列表
     */
    @GetMapping("/engineers/{engineerId}")
    public Result<?> getEngineerDetail(@PathVariable Long engineerId) {
        try {
            Map<String, Object> result = contactsService.getEngineerDetailWithTasks(engineerId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取工程师详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取非工程师角色的联系人列表
     */
    @GetMapping("/other")
    public Result<?> getOtherContactsList(
            @RequestParam(required = false) String keyword) {
        try {
            // 使用JdbcTemplate直接执行SQL查询
            String sql = "SELECT u.id, u.work_id, u.name, u.department, r.code as role_code " +
                    "FROM [user] u " +
                    "JOIN user_role ur ON u.id = ur.user_id " +
                    "JOIN role r ON ur.role_id = r.id " +
                    "WHERE r.code != 'ROLE_ENGINEER'";
            
            // 添加关键字搜索条件
            if (keyword != null && !keyword.isEmpty()) {
                sql += " AND (u.name LIKE ? OR u.work_id LIKE ? OR u.department LIKE ?)";
                String likePattern = "%" + keyword + "%";
                return Result.success(jdbcTemplate.queryForList(sql, likePattern, likePattern, likePattern));
            } else {
                return Result.success(jdbcTemplate.queryForList(sql));
            }
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取联系人详情
     */
    @GetMapping("/detail/{userId}")
    public Result<ContactDTO> getContactDetail(@PathVariable Long userId) {
        Long currentUserId = UserContextHolder.getUserId();
        ContactDTO contactDTO = contactsService.getContactDetail(currentUserId, userId);
        return Result.success(contactDTO);
    }
    
    /**
     * 获取联系人分组列表
     */
    @GetMapping("/groups")
    public Result<List<ContactGroupDTO>> getContactGroups() {
        Long userId = UserContextHolder.getUserId();
        List<ContactGroupDTO> groups = contactsService.getContactGroups(userId);
        return Result.success(groups);
    }
    
    /**
     * 创建联系人分组
     */
    @PostMapping("/groups")
    public Result<ContactGroupDTO> createContactGroup(@RequestBody Map<String, String> params) {
        Long userId = UserContextHolder.getUserId();
        String name = params.get("name");
        ContactGroupDTO groupDTO = contactsService.createContactGroup(userId, name);
        return Result.success(groupDTO);
    }
    
    /**
     * 更新联系人分组
     */
    @PutMapping("/groups")
    public Result<ContactGroupDTO> updateContactGroup(@RequestBody Map<String, Object> params) {
        Long userId = UserContextHolder.getUserId();
        Long groupId = Long.parseLong(params.get("id").toString());
        String name = params.get("name").toString();
        ContactGroupDTO groupDTO = contactsService.updateContactGroup(groupId, userId, name);
        return Result.success(groupDTO);
    }
    
    /**
     * 删除联系人分组
     */
    @DeleteMapping("/groups/{groupId}")
    public Result<Boolean> deleteContactGroup(@PathVariable Long groupId) {
        Long userId = UserContextHolder.getUserId();
        boolean result = contactsService.deleteContactGroup(groupId, userId);
        return Result.success(result);
    }
    
    /**
     * 向分组添加联系人
     */
    @PostMapping("/groups/add")
    public Result<ContactGroupDTO> addContactsToGroup(@RequestBody Map<String, Object> params) {
        Long userId = UserContextHolder.getUserId();
        Long groupId = Long.parseLong(params.get("groupId").toString());
        @SuppressWarnings("unchecked")
        List<Long> contactIds = (List<Long>) params.get("contactIds");
        ContactGroupDTO groupDTO = contactsService.addContactsToGroup(groupId, userId, contactIds);
        return Result.success(groupDTO);
    }
    
    /**
     * 从分组移除联系人
     */
    @PostMapping("/groups/remove")
    public Result<ContactGroupDTO> removeContactsFromGroup(@RequestBody Map<String, Object> params) {
        Long userId = UserContextHolder.getUserId();
        Long groupId = Long.parseLong(params.get("groupId").toString());
        @SuppressWarnings("unchecked")
        List<Long> contactIds = (List<Long>) params.get("contactIds");
        ContactGroupDTO groupDTO = contactsService.removeContactsFromGroup(groupId, userId, contactIds);
        return Result.success(groupDTO);
    }
} 