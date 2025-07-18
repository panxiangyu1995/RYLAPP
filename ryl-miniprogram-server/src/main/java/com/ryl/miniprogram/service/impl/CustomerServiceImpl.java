package com.ryl.miniprogram.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ryl.miniprogram.entity.Customer;
import com.ryl.miniprogram.exception.BusinessException;
import com.ryl.miniprogram.mapper.CustomerMapper;
import com.ryl.miniprogram.service.CustomerService;
import com.ryl.miniprogram.util.JwtTokenUtil;
import com.ryl.miniprogram.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.util.DigestUtils;

/**
 * 客户服务实现类
 */
@Slf4j
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private WxMaService wxMaService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Override
    public Customer getById(Long id) {
        return customerMapper.selectById(id);
    }
    
    @Override
    public Customer getByOpenid(String openid) {
        return customerMapper.selectByOpenid(openid);
    }
    
    @Override
    public Customer getByContact(String contact) {
        return customerMapper.selectByContact(contact);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Customer customer) {
        customer.setCreateTime(new Date());
        customer.setUpdateTime(new Date());
        return customerMapper.insert(customer) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Customer customer) {
        try {
            customer.setUpdateTime(new Date());
            int rows = customerMapper.updateById(customer);
            log.debug("更新客户数据，影响行数: {}, id: {}", rows, customer.getId());
            return rows > 0;
        } catch (Exception e) {
            log.error("更新客户数据异常，id: {}", customer.getId(), e);
            throw e;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWechatInfo(Customer customer) {
        return customerMapper.updateWechatInfo(customer) > 0;
    }
    
    @Override
    public PageVO<Customer> listByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Customer> list = customerMapper.selectList(new QueryWrapper<>());
        PageInfo<Customer> pageInfo = new PageInfo<>(list);
        
        PageVO<Customer> pageVO = new PageVO<>();
        pageVO.setPageNum(pageInfo.getPageNum());
        pageVO.setPageSize(pageInfo.getPageSize());
        pageVO.setTotalPage(pageInfo.getPages());
        pageVO.setTotal(pageInfo.getTotal());
        pageVO.setList(pageInfo.getList());
        return pageVO;
    }

    @Override
    public List<Customer> listAll() {
        return customerMapper.selectList(null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object login(String code, String nickname, String avatarUrl, String phoneCode) {
        try {
            // 1. 获取微信用户的openid和session_key
            WxMaJscode2SessionResult sessionResult = wxMaService.getUserService().getSessionInfo(code);
            String openid = sessionResult.getOpenid();
            String sessionKey = sessionResult.getSessionKey();

            // 2. 解密手机号 (如果提供了phoneCode)
            String phoneNumber = null;
            if (phoneCode != null && !phoneCode.isEmpty()) {
                WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(phoneCode);
                phoneNumber = phoneNoInfo.getPhoneNumber();
            }

            // 3. 查询用户是否已存在
            Customer customer = getByOpenid(openid);

            // 4. 用户不存在，创建新用户
            if (customer == null) {
                customer = new Customer();
                customer.setOpenid(openid);
                customer.setSessionKey(sessionKey);
                customer.setLastLoginTime(new Date());
                customer.setCreateTime(new Date());
                customer.setUpdateTime(new Date());
                
                // 使用前端传递的信息填充
                customer.setContact(nickname); // 微信昵称存入contact
                customer.setAvatarUrl(avatarUrl); // 修正：调用正确的 setAvatarUrl
                customer.setPhone(phoneNumber); // 真实手机号

                save(customer);
            } else {
                // 用户已存在，更新session_key和最后登录时间
                customer.setSessionKey(sessionKey);
                customer.setLastLoginTime(new Date());
                updateById(customer);
            }

            // 5. 生成token
            String token = jwtTokenUtil.generateToken(openid, customer.getId());

            // 6. 返回token和用户信息
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", customer);

            return result;

        } catch (Exception e) {
            log.error("微信登录失败", e);
            throw new BusinessException("微信登录失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object loginWithTestOpenid(String openid) {
        log.info("使用测试openid登录: {}", openid);
        
        try {
            // 查询用户是否已存在
            Customer customer = getByOpenid(openid);
            
            // 用户不存在，创建测试用户
            if (customer == null) {
                log.warn("测试用户不存在，创建新的测试用户: {}", openid);
                customer = new Customer();
                customer.setOpenid(openid);
                customer.setSessionKey("test_session_key");
                customer.setLastLoginTime(new Date());
                customer.setCreateTime(new Date());
                customer.setUpdateTime(new Date());
                // 设置测试用户名称
                if (openid.equals("test_openid_123456")) {
                    customer.setName("测试客户A");
                } else if (openid.equals("test_openid_234567")) {
                    customer.setName("测试客户B");
                } else if (openid.equals("test_openid_345678")) {
                    customer.setName("测试客户C");
                } else {
                    customer.setName("测试客户");
                }
                save(customer);
            } else {
                // 更新登录时间
                log.info("测试用户已存在: {}", customer.getName());
                customer.setLastLoginTime(new Date());
                updateWechatInfo(customer);
            }
            
            // 生成token
            String token = jwtTokenUtil.generateToken(openid, customer.getId());
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", customer);
            return result;
        } catch (Exception e) {
            log.error("测试登录失败", e);
            throw new BusinessException("测试登录失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object loginWithPassword(String contact, String password) {
        log.info("用户名密码登录: {}", contact);
        
        try {
            // 查询用户是否存在
            Customer customer = getByContact(contact);
            if (customer == null) {
                throw new BusinessException("用户名或密码错误");
            }
            
            // 验证密码
            String encryptedPassword = encryptPassword(password, customer.getSalt());
            if (!encryptedPassword.equals(customer.getPassword())) {
                throw new BusinessException("用户名或密码错误");
            }
            
            // 更新登录时间
            customer.setLastLoginTime(new Date());
            customerMapper.updateLoginInfo(customer);
            
            // 生成token
            String token = jwtTokenUtil.generateToken(customer.getContact(), customer.getId());
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", customer);
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("账号密码登录失败", e);
            throw new BusinessException("账号密码登录失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(Customer customer) {
        if (customer == null) {
            throw new BusinessException("注册信息不能为空");
        }

        if (StringUtils.isBlank(customer.getContact())) {
            throw new BusinessException("用户名不能为空");
        }

        if (StringUtils.isBlank(customer.getPassword())) {
            throw new BusinessException("密码不能为空");
        }

        // 检查用户名是否已存在
        if (checkContactExists(customer.getContact())) {
            throw new BusinessException("用户名已存在");
        }

        // 密码加密
        String salt = UUID.randomUUID().toString().replaceAll("-", "");
        String md5Password = DigestUtils.md5DigestAsHex((customer.getPassword() + salt).getBytes());
        customer.setPassword(md5Password);
        customer.setSalt(salt);

        return save(customer);
    }
    
    @Override
    public boolean checkContactExists(String contact) {
        Customer customer = getByContact(contact);
        return customer != null;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserInfo(Customer customer, Long userId) {
        log.info("服务层更新用户资料，userId: {}, 客户信息: {}", userId, customer);
        try {
            Customer existCustomer = getById(userId);
            if (existCustomer == null) {
                log.error("用户不存在，userId: {}", userId);
                throw new BusinessException("用户不存在");
            }
            
            // 只更新允许修改的字段
            existCustomer.setName(customer.getName());
            existCustomer.setContact(customer.getContact());
            existCustomer.setPhone(customer.getPhone());
            existCustomer.setEmail(customer.getEmail());
            existCustomer.setAddress(customer.getAddress());
            existCustomer.setDepartment(customer.getDepartment());
            existCustomer.setPosition(customer.getPosition());
            
            // 更新头像URL - 注意在Customer实体类中，avatarUrl字段实际上是映射到数据库的wechat_avatar_url列
            // 前端可能同时发送了avatarUrl和wechat_avatar_url，我们只需要保存一个即可
            if (customer.getAvatarUrl() != null && !customer.getAvatarUrl().isEmpty()) {
                existCustomer.setAvatarUrl(customer.getAvatarUrl());
                log.info("更新用户头像URL: {}", customer.getAvatarUrl());
            }
            
            existCustomer.setUpdateTime(new Date());
            
            log.debug("更新前用户数据: {}", existCustomer);
            boolean result = update(existCustomer);
            log.info("用户资料更新结果: {}, userId: {}", result ? "成功" : "失败", userId);
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户资料异常，userId: {}", userId, e);
            throw new BusinessException("更新用户资料失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer updateAvatar(Long userId, String avatarUrl) {
        log.info("更新用户头像，userId: {}, avatarUrl: {}", userId, avatarUrl);
        try {
            Customer existCustomer = getById(userId);
            if (existCustomer == null) {
                log.error("用户不存在，userId: {}", userId);
                throw new BusinessException("用户不存在");
            }
            
            // 更新头像URL - 注意在Customer实体类中，avatarUrl字段实际上是映射到数据库的wechat_avatar_url列
            existCustomer.setAvatarUrl(avatarUrl);
            existCustomer.setUpdateTime(new Date());
            
            boolean result = update(existCustomer);
            log.info("用户头像更新结果: {}, userId: {}", result ? "成功" : "失败", userId);

            if (!result) {
                throw new BusinessException("更新用户头像失败");
            }
            
            // 返回更新后的完整用户信息
            return getById(userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户头像异常，userId: {}", userId, e);
            throw new BusinessException("更新用户头像失败：" + e.getMessage());
        }
    }
    
    /**
     * 加密密码
     * @param password 明文密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    private String encryptPassword(String password, String salt) {
        return DigestUtil.md5Hex(password + salt);
    }
} 