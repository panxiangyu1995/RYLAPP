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
import com.ryl.engineer.entity.User;
import com.ryl.engineer.service.UserService;
import com.ryl.engineer.util.JwtUtil;
import com.ryl.engineer.util.PasswordUtil;
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

    @Autowired
    private UserService userService;
    
    // 从配置中获取上传路径
    @Value("${app.upload.base-path:}")
    private String baseUploadPath;
    
    // 从配置中获取头像路径
    @Value("${app.upload.avatar-path:avatars}")
    private String avatarPath;
    
    // 从配置中获取头像URL前缀
    @Value("${app.upload.avatar-url-prefix:http://localhost:8089/api/v1/uploads/avatars}")
    private String avatarUrlPrefix;

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
     * @param request HTTP请求
     * @return 用户信息响应
     */
    @GetMapping("/profile")
    public Result<UserInfoResponse> getUserProfile(HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 从token中获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
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
        response.setAvatar(user.getAvatar());
        response.setTechnicalCategory(user.getTechnicalCategory());

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
     * @param request HTTP请求
     * @return 用户信息响应
     */
    @GetMapping("/info")
    public Result<UserInfoResponse> getUserInfo(HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 从token中获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
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
        response.setAvatar(user.getAvatar());
        response.setTechnicalCategory(user.getTechnicalCategory());
        
        return Result.success(response);
    }
    
    /**
     * 更新用户个人信息
     *
     * @param request 用户信息请求
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @PutMapping("/profile")
    public Result<Map<String, Object>> updateUserProfile(@RequestBody @Validated UserInfoRequest request, HttpServletRequest httpRequest) {
        // 从token中获取用户ID
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
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
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @PostMapping("/info")
    public Result<UserInfoResponse> updateUserInfo(@RequestBody @Validated UserInfoRequest request, HttpServletRequest httpRequest) {
        // 从token中获取用户ID
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
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
        response.setAvatar(currentUser.getAvatar());
        response.setTechnicalCategory(currentUser.getTechnicalCategory());

        return Result.success(response);
    }
    
    /**
     * 更新用户头像
     *
     * @param file 头像文件
     * @param request HTTP请求
     * @return 更新结果
     */
    @PostMapping("/avatar")
    public Result<Map<String, Object>> updateAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 从token中获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        // 获取用户信息
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 检查文件是否为空
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        
        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只支持上传图片文件");
            }
            
            // 获取文件后缀
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            // 检查文件后缀
            if (!".jpg".equalsIgnoreCase(suffix) && !".jpeg".equalsIgnoreCase(suffix) 
                && !".png".equalsIgnoreCase(suffix) && !".gif".equalsIgnoreCase(suffix)) {
                return Result.error("只支持JPG、JPEG、PNG和GIF格式的图片");
            }
            
            // 限制文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("文件大小不能超过5MB");
            }
            
            // 使用类级别的配置变量
            String effectiveBasePath = baseUploadPath;
            
            // 如果配置为空，使用默认路径
            if (effectiveBasePath == null || effectiveBasePath.isEmpty()) {
                effectiveBasePath = System.getProperty("user.dir") + "/uploads";
            }
            
            // 创建文件存储目录
            String avatarDir = effectiveBasePath + "/" + avatarPath + "/";
            File dir = new File(avatarDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    return Result.error("创建上传目录失败");
                }
            }
            
            // 生成新的文件名
            String newFileName = "avatar_" + userId + "_" + System.currentTimeMillis() + suffix;
            String filePath = avatarDir + newFileName;
            
            // 保存文件
            File dest = new File(filePath);
            file.transferTo(dest);
            
            // 构建头像URL
            String avatarUrl = avatarUrlPrefix + "/" + newFileName;
            
            // 更新用户头像URL
            user.setAvatar(avatarUrl);
            boolean success = userService.updateUser(user);
            
            if (!success) {
                return Result.error("更新头像失败");
            }
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("updated", true);
            response.put("avatarUrl", avatarUrl);
            
            return Result.success(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param request HTTP请求
     * @return 修改结果
     */
    @PutMapping("/password")
    public Result<Map<String, Object>> updatePassword(@RequestBody Map<String, String> passwordData, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 从token中获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
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