package com.ryl.engineer.service.impl;

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
} 