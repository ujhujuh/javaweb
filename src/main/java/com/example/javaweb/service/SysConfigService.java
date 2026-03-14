package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.SysConfig;

import java.util.List;

public interface SysConfigService extends IService<SysConfig> {

    IPage<SysConfig> selectConfigList(IPage<SysConfig> page, SysConfig config);

    SysConfig selectConfigById(Long configId);

    boolean insertConfig(SysConfig config);

    boolean updateConfig(SysConfig config);

    boolean deleteConfigByIds(List<Long> configIds);

    boolean checkConfigKeyUnique(SysConfig config);

    String selectConfigByKey(String configKey);

    boolean updateConfigByKey(String configKey, String configValue);
}