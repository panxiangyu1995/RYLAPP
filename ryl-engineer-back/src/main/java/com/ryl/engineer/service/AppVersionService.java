package com.ryl.engineer.service;

import com.ryl.engineer.vo.AppVersionVO;

/**
 * App版本服务接口
 * @author: RYL
 */
public interface AppVersionService {
    /**
     * 获取最新版本信息
     * @return AppVersionVO
     */
    AppVersionVO getLatestVersion();
} 