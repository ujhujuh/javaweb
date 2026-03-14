package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.SysOperLog;
import com.example.javaweb.mapper.SysOperLogMapper;
import com.example.javaweb.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {

    @Autowired
    private SysOperLogMapper sysOperLogMapper;

    @Override
    public IPage<SysOperLog> selectOperLogList(IPage<SysOperLog> page, SysOperLog operLog) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(operLog.getTitle()), SysOperLog::getTitle, operLog.getTitle())
                .like(StringUtils.hasText(operLog.getOperName()), SysOperLog::getOperName, operLog.getOperName())
                .like(StringUtils.hasText(operLog.getOperIp()), SysOperLog::getOperIp, operLog.getOperIp())
                .eq(operLog.getStatus() != null, SysOperLog::getStatus, operLog.getStatus())
                .orderByDesc(SysOperLog::getOperTime);
        return sysOperLogMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOperLogByIds(Long[] operIds) {
        return sysOperLogMapper.deleteBatchIds(Arrays.asList(operIds)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cleanOperLog() {
        return sysOperLogMapper.delete(null) > 0;
    }
}