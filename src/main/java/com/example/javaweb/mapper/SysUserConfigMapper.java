package com.example.javaweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javaweb.entity.SysUserConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户配置 Mapper
 */
@Mapper
public interface SysUserConfigMapper extends BaseMapper<SysUserConfig> {
}