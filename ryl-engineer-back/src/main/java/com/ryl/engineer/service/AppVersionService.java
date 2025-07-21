package com.ryl.engineer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryl.engineer.dto.AppVersionCreateDTO;
import com.ryl.engineer.dto.AppVersionUpdateDTO;
import com.ryl.engineer.entity.AppVersion;
import com.ryl.engineer.vo.AppVersionVO;

/**
 * App版本服务接口
 * @author: RYL
 */
public interface AppVersionService {
    /**
     * 获取最新版本信息(供APP端调用)
     * @return AppVersionVO
     */
    AppVersionVO getLatestVersion();

    /**
     * 【后台】创建新版本
     */
    AppVersion createVersion(AppVersionCreateDTO createDTO);

    /**
     * 【后台】分页查询版本列表
     */
    Page<AppVersion> getVersionList(Page<AppVersion> page);

    /**
     * 【后台】更新版本信息
     */
    AppVersion updateVersion(Long id, AppVersionUpdateDTO updateDTO);

    /**
     * 【后台】删除版本
     */
    void deleteVersion(Long id);
} 