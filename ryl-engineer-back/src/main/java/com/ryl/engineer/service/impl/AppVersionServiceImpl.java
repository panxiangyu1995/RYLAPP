package com.ryl.engineer.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryl.engineer.dto.AppVersionCreateDTO;
import com.ryl.engineer.dto.AppVersionUpdateDTO;
import com.ryl.engineer.entity.AppVersion;
import com.ryl.engineer.mapper.AppVersionMapper;
import com.ryl.engineer.service.AppVersionService;
import com.ryl.engineer.vo.AppVersionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * App版本服务实现类
 * @author: RYL
 */
@Service
public class AppVersionServiceImpl implements AppVersionService {

    @Resource
    private AppVersionMapper appVersionMapper;

    @Override
    public AppVersionVO getLatestVersion() {
        AppVersion latestVersion = appVersionMapper.findLatest();
        if (latestVersion == null) {
            return null;
        }
        AppVersionVO vo = new AppVersionVO();
        BeanUtils.copyProperties(latestVersion, vo);
        return vo;
    }

    @Override
    public AppVersion createVersion(AppVersionCreateDTO createDTO) {
        AppVersion appVersion = new AppVersion();
        BeanUtils.copyProperties(createDTO, appVersion);
        appVersionMapper.insert(appVersion);
        return appVersion;
    }

    @Override
    public Page<AppVersion> getVersionList(Page<AppVersion> page) {
        return appVersionMapper.selectPage(page, null);
    }

    @Override
    public AppVersion updateVersion(Long id, AppVersionUpdateDTO updateDTO) {
        AppVersion appVersion = appVersionMapper.selectById(id);
        if (appVersion == null) {
            // 或抛出自定义异常
            return null;
        }
        BeanUtils.copyProperties(updateDTO, appVersion);
        appVersionMapper.updateById(appVersion);
        return appVersion;
    }

    @Override
    public void deleteVersion(Long id) {
        appVersionMapper.deleteById(id);
    }
} 