package com.example.javaweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.SysUserConfig;

/**
 * 用户配置服务接口
 */
public interface SysUserConfigService extends IService<SysUserConfig> {

    /**
     * 获取用户配置
     * @param userId 用户ID
     * @param configKey 配置键
     * @return 配置值
     */
    String getUserConfig(Long userId, String configKey);

    /**
     * 设置用户配置
     * @param userId 用户ID
     * @param configKey 配置键
     * @param configValue 配置值
     */
    void setUserConfig(Long userId, String configKey, String configValue);

    /**
     * 删除用户配置
     * @param userId 用户ID
     * @param configKey 配置键
     */
    void deleteUserConfig(Long userId, String configKey);
}