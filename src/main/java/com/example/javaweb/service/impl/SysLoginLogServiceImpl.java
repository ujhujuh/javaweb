package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.SysLoginLog;
import com.example.javaweb.mapper.SysLoginLogMapper;
import com.example.javaweb.service.SysLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    @Autowired
    private SysLoginLogMapper sysLoginLogMapper;

    @Override
    public IPage<SysLoginLog> selectLoginLogList(IPage<SysLoginLog> page, SysLoginLog loginLog) {
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(loginLog.getUsername()), SysLoginLog::getUsername, loginLog.getUsername())
                .like(StringUtils.hasText(loginLog.getIpaddr()), SysLoginLog::getIpaddr, loginLog.getIpaddr())
                .eq(StringUtils.hasText(loginLog.getStatus()), SysLoginLog::getStatus, loginLog.getStatus())
                .orderByDesc(SysLoginLog::getLoginTime);
        return sysLoginLogMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLoginLogByIds(Long[] infoIds) {
        return sysLoginLogMapper.deleteBatchIds(Arrays.asList(infoIds)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cleanLoginLog() {
        return sysLoginLogMapper.delete(null) > 0;
    }
}