package com.ryl.engineer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryl.common.response.Result;
import com.ryl.engineer.dto.AppVersionCreateDTO;
import com.ryl.engineer.dto.AppVersionUpdateDTO;
import com.ryl.engineer.entity.AppVersion;
import com.ryl.engineer.service.AppVersionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 后台管理 - APP版本管理接口
 * @author RYL
 */
@RestController
@RequestMapping("/api/admin/versions")
public class AppVersionAdminController {

    @Resource
    private AppVersionService appVersionService;

    @PostMapping
    public Result<AppVersion> createVersion(@Valid @RequestBody AppVersionCreateDTO createDTO) {
        AppVersion newVersion = appVersionService.createVersion(createDTO);
        return Result.success(newVersion);
    }

    @GetMapping
    public Result<Page<AppVersion>> getVersionList(Page<AppVersion> page) {
        Page<AppVersion> versionPage = appVersionService.getVersionList(page);
        return Result.success(versionPage);
    }

    @PutMapping("/{id}")
    public Result<AppVersion> updateVersion(@PathVariable Long id, @RequestBody AppVersionUpdateDTO updateDTO) {
        AppVersion updatedVersion = appVersionService.updateVersion(id, updateDTO);
        return Result.success(updatedVersion);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteVersion(@PathVariable Long id) {
        appVersionService.deleteVersion(id);
        return Result.success();
    }
} 