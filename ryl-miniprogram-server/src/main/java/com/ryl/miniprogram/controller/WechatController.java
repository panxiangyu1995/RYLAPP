package com.ryl.miniprogram.controller;

import com.ryl.miniprogram.dto.WxLoginDTO;
import com.ryl.miniprogram.security.NoAuth;
import com.ryl.miniprogram.service.CustomerService;
import com.ryl.miniprogram.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 微信相关接口控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/wechat")
public class WechatController {
    
    @Autowired
    private CustomerService customerService;
    
    /**
     * 微信登录
     */
    @NoAuth
    @PostMapping("/login")
    public ResultVO<?> login(@RequestBody @Validated WxLoginDTO loginDTO) {
        String code = loginDTO.getCode();
        log.info("微信登录：{}", code);
        
        // 检查是否为测试code (以TEST_开头)
        if (code != null && code.startsWith("TEST_")) {
            log.info("检测到测试登录code: {}", code);
            // 从code中提取openid部分，例如TEST_123456 -> test_openid_123456
            String openid = "test_openid_" + code.substring(5);
            log.info("使用测试openid: {}", openid);
            // 调用测试登录方法
            Object result = customerService.loginWithTestOpenid(openid);
            return ResultVO.success(result);
        }
        
        // 正常微信登录流程
        Object result = customerService.login(loginDTO.getCode(), loginDTO.getNickname(), loginDTO.getAvatarUrl(), loginDTO.getPhoneCode());
        return ResultVO.success(result);
    }
} 