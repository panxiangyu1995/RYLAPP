package com.ryl.engineer.service.impl;

import com.ryl.engineer.dto.ForgotPasswordResponse;
import com.ryl.engineer.dto.LoginRequest;
import com.ryl.engineer.dto.LoginResponse;
import com.ryl.engineer.dto.RegisterRequest;
import com.ryl.engineer.dto.RegisterResponse;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.engineer.service.FileService;
import com.ryl.engineer.service.UserService;
import com.ryl.engineer.util.JwtUtil;
import com.ryl.engineer.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import com.ryl.engineer.dto.FileStorageInfo;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileService fileService; // 注入统一的文件服务

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            logger.info("开始登录处理，工号：{}", request.getWorkId());
            
            // 根据工号查询用户
            User user = userMapper.selectByWorkId(request.getWorkId());
            
            // 用户不存在
            if (user == null) {
                logger.warn("登录失败：用户不存在，工号：{}", request.getWorkId());
                return null;
            }
            
            // 密码错误
            if (!PasswordUtil.verify(request.getPassword(), user.getPassword())) {
                logger.warn("登录失败：密码错误，工号：{}", request.getWorkId());
                return null;
            }
            
            logger.info("用户验证成功，更新最后登录时间，用户ID：{}", user.getId());
            
            // 更新最后登录时间
            try {
                userMapper.updateLastLoginTime(user.getId());
            } catch (Exception e) {
                logger.error("更新最后登录时间失败", e);
                // 继续处理，不影响登录流程
            }
            
            // 生成token
            String token = JwtUtil.generateToken(user.getId(), user.getWorkId());
            
            // 计算令牌过期时间（30天）
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 30);
            Date expireDate = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            String tokenExpire = sdf.format(expireDate);
            
            logger.info("生成token成功，更新数据库中的token信息");
            
            // 将token和过期时间保存到数据库
            try {
                userMapper.updateToken(user.getId(), token, expireDate);
            } catch (Exception e) {
                logger.error("更新token失败", e);
                // 继续处理，不影响登录流程
            }
            
            // 构建登录响应
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setTokenExpire(tokenExpire);
            response.setWorkId(user.getWorkId());
            response.setName(user.getName());
            response.setMobile(user.getMobile());
            response.setDepartment(user.getDepartment());
            response.setLocation(user.getLocation());
            response.setAvatar(user.getAvatar());
            
            logger.info("登录成功，用户：{}", user.getWorkId());
            return response;
        } catch (Exception e) {
            logger.error("登录过程中发生异常", e);
            return null;
        }
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 检查工号是否已存在
        User existUser = userMapper.selectByWorkId(request.getWorkId());
        if (existUser != null) {
            return null;
        }
        
        // 检查手机号是否已存在
        existUser = userMapper.selectByMobile(request.getMobile());
        if (existUser != null) {
            return null;
        }
        
        // 检查密码和确认密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return null;
        }
        
        // 创建新用户
        User user = new User();
        user.setWorkId(request.getWorkId());
        user.setName(request.getName());
        user.setMobile(request.getMobile());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setDepartment(request.getDepartment());
        user.setLocation(request.getLocation());
        user.setStatus(1); // 1-正常
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        
        // 插入用户
        boolean success = userMapper.insert(user) > 0;
        
        if (success) {
            RegisterResponse response = new RegisterResponse();
            response.setRegistered(true);
            response.setWorkId(user.getWorkId());
            response.setName(user.getName());
            return response;
        }
        
        return null;
    }

    @Override
    public ForgotPasswordResponse forgotPassword(String workId, String mobile) {
        // 根据工号查询用户
        User user = userMapper.selectByWorkId(workId);
        
        ForgotPasswordResponse response = new ForgotPasswordResponse();
        response.setReset(false);
        
        // 用户不存在或手机号不匹配
        if (user == null || !mobile.equals(user.getMobile())) {
            return response;
        }
        
        // 验证通过，可以重置密码
        response.setReset(true);
        response.setWorkId(workId);
        
        return response;
    }

    @Override
    @Transactional
    public boolean resetPassword(String workId, String newPassword) {
        // 根据工号查询用户
        User user = userMapper.selectByWorkId(workId);
        
        // 用户不存在
        if (user == null) {
            return false;
        }
        
        // 更新密码
        user.setPassword(PasswordUtil.encode(newPassword));
        user.setUpdateTime(new Date());
        
        return userMapper.update(user) > 0;
    }

    @Override
    public User getUserByWorkId(String workId) {
        return userMapper.selectByWorkId(workId);
    }

    @Override
    public User getUserByMobile(String mobile) {
        return userMapper.selectByMobile(mobile);
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        user.setUpdateTime(new Date());
        return userMapper.update(user) > 0;
    }

    @Override
    @Transactional
    public String updateUserAvatar(Long userId, MultipartFile file) throws IOException {
        // 1. 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 2. 校验文件
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只支持上传图片文件");
        }
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        // 3. 调用统一文件服务上传文件
        FileStorageInfo storageInfo = fileService.uploadFile(file, "avatars");

        // 4. 更新用户信息
        user.setAvatar(storageInfo.getRelativePath()); // 存储相对路径
        user.setUpdateTime(new java.util.Date()); // 修正为Date类型
        userMapper.update(user); // 使用自定义的update方法

        // 5. 返回完整的URL给前端
        return storageInfo.getFullUrl();
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    @Override
    public List<Map<String, Object>> getEngineersList(Map<String, Object> params) {
        List<User> engineers = userMapper.selectEngineers(params);
        return convertUserListToMapList(engineers);
    }
    
    @Override
    public List<Map<String, Object>> getSalesList(Map<String, Object> params) {
        List<User> salesPersons = userMapper.selectSales(params);
        return convertUserListToMapList(salesPersons);
    }
    
    /**
     * 将用户列表转换为Map列表，过滤敏感信息
     * 
     * @param users 用户列表
     * @return 过滤后的Map列表
     */
    private List<Map<String, Object>> convertUserListToMapList(List<User> users) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", user.getId());
                map.put("workId", user.getWorkId());
                map.put("name", user.getName());
                map.put("department", user.getDepartment());
                map.put("location", user.getLocation());
                map.put("mobile", user.getMobile());
                map.put("avatar", user.getAvatar());
                // 不返回密码等敏感信息
                
                result.add(map);
            }
        }
        
        return result;
    }
} 