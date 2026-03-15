package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.SysUserConfig;
import com.example.javaweb.mapper.SysUserConfigMapper;
import com.example.javaweb.service.SysUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 用户配置服务实现类
 */
@Service
public class SysUserConfigServiceImpl extends ServiceImpl<SysUserConfigMapper, SysUserConfig> implements SysUserConfigService {

    @Autowired
    private SysUserConfigMapper sysUserConfigMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CONFIG_CACHE_PREFIX = "user:config:";
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Override
    public String getUserConfig(Long userId, String configKey) {
        String cacheKey = USER_CONFIG_CACHE_PREFIX + userId + ":" + configKey;
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue != null) {
            return cachedValue.toString();
        }

        LambdaQueryWrapper<SysUserConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserConfig::getUserId, userId)
                .eq(SysUserConfig::getConfigKey, configKey);
        SysUserConfig config = sysUserConfigMapper.selectOne(wrapper);

        String value = config != null ? config.getConfigValue() : null;

        if (value != null) {
            redisTemplate.opsForValue().set(cacheKey, value, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }

        return value;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setUserConfig(Long userId, String configKey, String configValue) {
        LambdaQueryWrapper<SysUserConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserConfig::getUserId, userId)
                .eq(SysUserConfig::getConfigKey, configKey);
        SysUserConfig existingConfig = sysUserConfigMapper.selectOne(wrapper);

        if (existingConfig != null) {
            existingConfig.setConfigValue(configValue);
            sysUserConfigMapper.updateById(existingConfig);
        } else {
            SysUserConfig newConfig = new SysUserConfig();
            newConfig.setUserId(userId);
            newConfig.setConfigKey(configKey);
            newConfig.setConfigValue(configValue);
            sysUserConfigMapper.insert(newConfig);
        }

        // 更新缓存
        String cacheKey = USER_CONFIG_CACHE_PREFIX + userId + ":" + configKey;
        redisTemplate.opsForValue().set(cacheKey, configValue, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserConfig(Long userId, String configKey) {
        LambdaQueryWrapper<SysUserConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserConfig::getUserId, userId)
                .eq(SysUserConfig::getConfigKey, configKey);
        sysUserConfigMapper.delete(wrapper);

        // 删除缓存
        String cacheKey = USER_CONFIG_CACHE_PREFIX + userId + ":" + configKey;
        redisTemplate.delete(cacheKey);
    }
}