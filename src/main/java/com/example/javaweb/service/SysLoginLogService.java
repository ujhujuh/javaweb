package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.SysLoginLog;

public interface SysLoginLogService extends IService<SysLoginLog> {

    IPage<SysLoginLog> selectLoginLogList(IPage<SysLoginLog> page, SysLoginLog loginLog);

    boolean deleteLoginLogByIds(Long[] infoIds);

    boolean cleanLoginLog();
}