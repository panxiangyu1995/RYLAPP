package com.ryl.miniprogram.controller;

import com.ryl.miniprogram.dto.LoginDTO;
import com.ryl.miniprogram.dto.RegisterDTO;
import com.ryl.miniprogram.entity.Customer;
import com.ryl.miniprogram.security.NoAuth;
import com.ryl.miniprogram.service.CustomerService;
import com.ryl.miniprogram.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 账号相关接口控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/account")
public class AccountController {
    
    @Autowired
    private CustomerService customerService;
    
    /**
     * 账号密码登录
     */
    @NoAuth
    @PostMapping("/login")
    public ResultVO<?> login(@RequestBody @Validated LoginDTO loginDTO) {
        String contact = loginDTO.getContact();
        String password = loginDTO.getPassword();
        log.info("账号密码登录：{}", contact);
        
        Object result = customerService.loginWithPassword(contact, password);
        return ResultVO.success(result);
    }
    
    /**
     * 注册新用户
     */
    @NoAuth
    @PostMapping("/register")
    public ResultVO<?> register(@RequestBody @Validated RegisterDTO registerDTO) {
        log.info("注册新用户：{}", registerDTO.getContact());
        log.info("接收到注册数据 DTO: {}", registerDTO);

        // 检查用户名是否已存在
        if (customerService.checkContactExists(registerDTO.getContact())) {
            return ResultVO.failed("用户名已存在");
        }
        
        // 转换为Customer对象
        Customer customer = new Customer();
        BeanUtils.copyProperties(registerDTO, customer);
        // 由于 password 在 Customer 实体中被 @JsonIgnore，需要手动设置
        customer.setPassword(registerDTO.getPassword());

        boolean result = customerService.register(customer);
        if (result) {
            return ResultVO.success("注册成功");
        } else {
            return ResultVO.failed("注册失败");
        }
    }
    
    /**
     * 检查用户名是否已存在
     */
    @NoAuth
    @GetMapping("/check-contact")
    public ResultVO<?> checkContact(@RequestParam String contact) {
        boolean exists = customerService.checkContactExists(contact);
        return ResultVO.success(exists);
    }
} 