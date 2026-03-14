package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.SysConfig;
import com.example.javaweb.mapper.SysConfigMapper;
import com.example.javaweb.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public IPage<SysConfig> selectConfigList(IPage<SysConfig> page, SysConfig config) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(config.getConfigName()), SysConfig::getConfigName, config.getConfigName())
                .like(StringUtils.hasText(config.getConfigKey()), SysConfig::getConfigKey, config.getConfigKey())
                .eq(StringUtils.hasText(config.getConfigType()), SysConfig::getConfigType, config.getConfigType())
                .orderByAsc(SysConfig::getId);
        return sysConfigMapper.selectPage(page, wrapper);
    }

    @Override
    public SysConfig selectConfigById(Long configId) {
        return sysConfigMapper.selectById(configId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertConfig(SysConfig config) {
        return sysConfigMapper.insert(config) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfig(SysConfig config) {
        return sysConfigMapper.updateById(config) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConfigByIds(List<Long> configIds) {
        for (Long configId : configIds) {
            SysConfig config = sysConfigMapper.selectById(configId);
            if ("Y".equals(config.getConfigType())) {
                throw new RuntimeException("内置参数【" + config.getConfigName() + "】不能删除");
            }
        }
        return sysConfigMapper.deleteBatchIds(configIds) > 0;
    }

    @Override
    public boolean checkConfigKeyUnique(SysConfig config) {
        Long configId = config.getId() == null ? -1L : config.getId();
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, config.getConfigKey());
        SysConfig info = sysConfigMapper.selectOne(wrapper);
        if (info != null && !info.getId().equals(configId)) {
            return false;
        }
        return true;
    }

    @Override
    public String selectConfigByKey(String configKey) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        SysConfig config = sysConfigMapper.selectOne(wrapper);
        if (config != null) {
            return config.getConfigValue();
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfigByKey(String configKey, String configValue) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        SysConfig config = sysConfigMapper.selectOne(wrapper);
        if (config != null) {
            config.setConfigValue(configValue);
            return sysConfigMapper.updateById(config) > 0;
        }
        return false;
    }
}