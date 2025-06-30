package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    /**
     * 测试接口
     */
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello, RYL Engineer Backend!");
    }
} 