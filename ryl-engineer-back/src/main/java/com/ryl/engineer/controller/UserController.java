package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.dto.ForgotPasswordRequest;
import com.ryl.engineer.dto.ForgotPasswordResponse;
import com.ryl.engineer.dto.LoginRequest;
import com.ryl.engineer.dto.LoginResponse;
import com.ryl.engineer.dto.RegisterRequest;
import com.ryl.engineer.dto.RegisterResponse;
import com.ryl.engineer.dto.UserInfoRequest;
import com.ryl.engineer.dto.UserInfoResponse;
import com.ryl.engineer.dto.request.SetSecurityPasswordRequest;
import com.ryl.engineer.dto.request.ChangeSecurityPasswordRequest;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.engineer.service.UserService;
import com.ryl.engineer.util.JwtUtil;
import com.ryl.engineer.util.PasswordUtil;
import com.ryl.engineer.util.FileUrlConverter;
import com.ryl.engineer.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileUrlConverter fileUrlConverter;
    
    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Validated LoginRequest request) {
        LoginResponse response = userService.login(request);
        if (response == null) {
            return Result.error("工号或密码错误");
        }
        return Result.success(response);
    }

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<RegisterResponse> register(@RequestBody @Validated RegisterRequest request) {
        // 密码验证移至Service层，但此处保留以快速失败
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return Result.error("两次输入的密码不一致");
        }
        
        RegisterResponse response = userService.register(request);
        if (response == null || !response.getRegistered()) {
            String message = (response != null && response.getMessage() != null) ? response.getMessage() : "注册失败，请检查输入信息";
            return Result.error(message);
        }
        return Result.success(response);
    }

    /**
     * 忘记密码验证
     *
     * @param workId 工号
     * @param mobile 手机号
     * @return 忘记密码响应
     */
    @PostMapping("/forgot-password/verify")
    public Result<ForgotPasswordResponse> forgotPasswordVerify(@RequestParam String workId, @RequestParam String mobile) {
        ForgotPasswordResponse response = userService.forgotPassword(workId, mobile);
        if (!response.getReset()) {
            return Result.error("工号或手机号不匹配");
        }
        return Result.success(response);
    }

    /**
     * 忘记密码（前端完整请求）
     *
     * @param request 忘记密码请求
     * @return 重置结果
     */
    @PostMapping("/forgot-password")
    public Result<Void> forgotPassword(@RequestBody @Validated ForgotPasswordRequest request) {
        // 验证工号和手机号
        ForgotPasswordResponse verifyResponse = userService.forgotPassword(request.getWorkId(), request.getMobile());
        if (!verifyResponse.getReset()) {
            return Result.error("工号或手机号不匹配");
        }
        
        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return Result.error("两次输入的密码不一致");
        }
        
        // 重置密码
        boolean success = userService.resetPassword(request.getWorkId(), request.getNewPassword());
        if (!success) {
            return Result.error("重置密码失败");
        }
        return Result.success();
    }

    /**
     * 重置密码
     *
     * @param workId      工号
     * @param newPassword 新密码
     * @return 重置结果
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestParam String workId, @RequestParam String newPassword) {
        boolean success = userService.resetPassword(workId, newPassword);
        if (!success) {
            return Result.error("重置密码失败");
        }
        return Result.success();
    }
    
    /**
     * 用户登出
     *
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // 这里可以添加任何必要的登出逻辑，如清除会话等
        // 目前前端主要处理登出逻辑，后端仅提供API接口
        return Result.success();
    }
    
    /**
     * 获取用户个人信息
     *
     * @return 用户信息响应
     */
    @GetMapping("/profile")
    public Result<UserInfoResponse> getUserProfile() {
        // 从上下文中获取用户ID
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            return Result.error(401, "无效的凭证，请重新登录");
        }
        
        // 获取用户信息
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 构建响应
        UserInfoResponse response = new UserInfoResponse();
        response.setWorkId(user.getWorkId());
        response.setName(user.getName());
        response.setMobile(user.getMobile());
        response.setDepartment(user.getDepartment());
        response.setLocation(user.getLocation());
        response.setAvatar(fileUrlConverter.toFullUrl(user.getAvatar()));
        response.setTechnicalCategory(user.getTechnicalCategory());
        response.setHasSecurityPassword(user.getSecurityPassword() != null && !user.getSecurityPassword().isEmpty());
        response.setRoles(userMapper.selectRolesByUserId(userId));

        // 添加任务统计信息（如果需要的话）
        Map<String, Object> taskStats = new HashMap<>();
        taskStats.put("ongoing", 2);
        taskStats.put("completed", 3);
        taskStats.put("rating", 4.8);
        response.setTaskStats(taskStats);
        
        return Result.success(response);
    }
    
    /**
     * 获取用户个人信息（旧接口，保持兼容）
     *
     * @return 用户信息响应
     */
    @GetMapping("/info")
    public Result<UserInfoResponse> getUserInfo() {
        // 从上下文中获取用户ID
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            return Result.error(401, "无效的凭证，请重新登录");
        }
        
        // 获取用户信息
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 构建响应
        UserInfoResponse response = new UserInfoResponse();
        response.setWorkId(user.getWorkId());
        response.setName(user.getName());
        response.setMobile(user.getMobile());
        response.setDepartment(user.getDepartment());
        response.setLocation(user.getLocation());
        response.setAvatar(fileUrlConverter.toFullUrl(user.getAvatar()));
        response.setTechnicalCategory(user.getTechnicalCategory());
        // 在旧接口中也补充上角色和安全密码信息
        response.setHasSecurityPassword(user.getSecurityPassword() != null && !user.getSecurityPassword().isEmpty());
        response.setRoles(userMapper.selectRolesByUserId(userId));
        
        return Result.success(response);
    }
    
    /**
     * 更新用户个人信息
     *
     * @param request 用户信息请求
     * @return 更新结果
     */
    @PutMapping("/profile")
    public Result<Map<String, Object>> updateUserProfile(@RequestBody @Validated UserInfoRequest request) {
        // 从上下文中获取用户ID
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            return Result.error(401, "无效的凭证，请重新登录");
        }
        
        // 获取当前用户信息
        User currentUser = userService.getUserById(userId);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        // 更新用户信息
        currentUser.setName(request.getName());
        currentUser.setMobile(request.getMobile());
        currentUser.setDepartment(request.getDepartment());
        currentUser.setLocation(request.getLocation());
        // 更新技术分类
        currentUser.setTechnicalCategory(request.getTechnicalCategory());
        
        boolean success = userService.updateUser(currentUser);
        if (!success) {
            return Result.error("更新失败");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "更新成功");
        
        return Result.success(result);
    }
    
    /**
     * 更新用户个人信息（旧接口，保持兼容）
     *
     * @param request 用户信息请求
     * @return 更新结果
     */
    @PostMapping("/info")
    public Result<UserInfoResponse> updateUserInfo(@RequestBody @Validated UserInfoRequest request) {
        // 从上下文中获取用户ID
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            return Result.error(401, "无效的凭证，请重新登录");
        }

        // 获取当前用户信息
        User currentUser = userService.getUserById(userId);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        // 更新用户信息
        currentUser.setName(request.getName());
        currentUser.setMobile(request.getMobile());
        currentUser.setDepartment(request.getDepartment());
        currentUser.setLocation(request.getLocation());
        // 更新技术分类
        currentUser.setTechnicalCategory(request.getTechnicalCategory());

        boolean success = userService.updateUser(currentUser);
        if (!success) {
            return Result.error("更新失败");
        }

        // 构建响应
        UserInfoResponse response = new UserInfoResponse();
        response.setWorkId(currentUser.getWorkId());
        response.setName(currentUser.getName());
        response.setMobile(currentUser.getMobile());
        response.setDepartment(currentUser.getDepartment());
        response.setLocation(currentUser.getLocation());
        response.setAvatar(fileUrlConverter.toFullUrl(currentUser.getAvatar()));
        response.setTechnicalCategory(currentUser.getTechnicalCategory());

        return Result.success(response);
    }
    
    /**
     * 更新用户头像
     *
     * @param file 头像文件
     * @return 更新结果
     */
    @PostMapping("/avatar")
    public Result<Map<String, Object>> updateAvatar(@RequestParam("file") MultipartFile file) {
        // 从token中获取用户ID
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            return Result.error(401, "无效的凭证，请重新登录");
        }

        try {
            String avatarUrl = userService.updateUserAvatar(userId, file);
            Map<String, Object> response = new HashMap<>();
            response.put("updated", true);
            response.put("avatarUrl", avatarUrl);
            return Result.success(response);
        } catch (IOException e) {
            log.error("更新用户头像失败, userId: {}", userId, e);
            return Result.error(500, "更新头像失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        }
    }
    
    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @PutMapping("/password")
    public Result<Map<String, Object>> updatePassword(@RequestBody Map<String, String> passwordData) {
        // 从上下文中获取用户ID
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            return Result.error(401, "无效的凭证，请重新登录");
        }
        
        // 获取用户信息
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 获取密码数据
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        // 验证参数
        if (oldPassword == null || newPassword == null) {
            return Result.error("参数错误");
        }
        
        // 验证旧密码
        if (!PasswordUtil.verify(oldPassword, user.getPassword())) {
            return Result.error("当前密码错误");
        }
        
        // 更新密码
        user.setPassword(PasswordUtil.encode(newPassword));
        boolean success = userService.updateUser(user);
        
        if (!success) {
            return Result.error("修改密码失败");
        }
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("updated", true);
        
        return Result.success(response);
    }
    
    /**
     * 设置安全密码
     * @param request 请求体
     * @return Result
     */
    @PostMapping("/security-password/set")
    public Result<Void> setSecurityPassword(@RequestBody @Validated SetSecurityPasswordRequest request) {
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            return Result.error(401, "无效的凭证，请重新登录");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        
        var response = userService.setSecurityPassword(user.getWorkId(), request.getNewPassword());
        if (response.getCode() != 200) {
            return Result.error(response.getCode(), response.getMessage());
        }
        return Result.success();
    }

    /**
     * 修改安全密码
     * @param request 请求体
     * @return Result
     */
    @PutMapping("/security-password/change")
    public Result<Void> changeSecurityPassword(@RequestBody @Validated ChangeSecurityPasswordRequest request) {
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            return Result.error(401, "无效的凭证，请重新登录");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        var response = userService.changeSecurityPassword(user.getWorkId(), request.getOldPassword(), request.getNewPassword());
        if (response.getCode() != 200) {
            return Result.error(response.getCode(), response.getMessage());
        }
        return Result.success();
    }
    
    /**
     * 获取工程师列表
     * @param params 查询参数
     * @return 工程师列表
     */
    @GetMapping("/engineers")
    public Result<List<Map<String, Object>>> getEngineersList(@RequestParam(required = false) Map<String, Object> params) {
        try {
            List<Map<String, Object>> engineers = userService.getEngineersList(params);
            return Result.success(engineers);
        } catch (Exception e) {
            return Result.error("获取工程师列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取销售人员列表
     * @param params 查询参数
     * @return 销售人员列表
     */
    @GetMapping("/sales")
    public Result<List<Map<String, Object>>> getSalesList(@RequestParam(required = false) Map<String, Object> params) {
        try {
            List<Map<String, Object>> salesList = userService.getSalesList(params);
            return Result.success(salesList);
        } catch (Exception e) {
            return Result.error("获取销售人员列表失败: " + e.getMessage());
        }
    }
} 