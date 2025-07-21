package com.ryl.engineer.controller;

import com.ryl.common.response.Result;
import com.ryl.engineer.service.AppVersionService;
import com.ryl.engineer.vo.AppVersionVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * App相关接口
 * @author RYL
 */
@RestController
@RequestMapping("/api/app")
public class AppController {

    @Resource
    private AppVersionService appVersionService;

    /**
     * 获取最新版本信息
     * @return Result<AppVersionVO>
     */
    @GetMapping("/latest-version")
    public Result<AppVersionVO> getLatestVersion() {
        AppVersionVO latestVersion = appVersionService.getLatestVersion();
        return Result.success(latestVersion);
    }
} 