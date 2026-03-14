package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.SysOperLog;

public interface SysOperLogService extends IService<SysOperLog> {

    IPage<SysOperLog> selectOperLogList(IPage<SysOperLog> page, SysOperLog operLog);

    boolean deleteOperLogByIds(Long[] operIds);

    boolean cleanOperLog();
}