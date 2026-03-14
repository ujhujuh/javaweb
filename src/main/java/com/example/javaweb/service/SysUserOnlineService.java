package com.example.javaweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.SysUserOnline;

import java.util.List;

public interface SysUserOnlineService extends IService<SysUserOnline> {

    List<SysUserOnline> selectOnlineList(String loginName, String ipaddr, String loginLocation);

    boolean forceLogout(String sessionId);
}