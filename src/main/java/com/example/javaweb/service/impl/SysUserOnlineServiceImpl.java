package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.SysUserOnline;
import com.example.javaweb.mapper.SysUserOnlineMapper;
import com.example.javaweb.service.SysUserOnlineService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysUserOnlineServiceImpl extends ServiceImpl<SysUserOnlineMapper, SysUserOnline> implements SysUserOnlineService {

    @Override
    public List<SysUserOnline> selectOnlineList(String loginName, String ipaddr, String loginLocation) {
        LambdaQueryWrapper<SysUserOnline> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(loginName)) {
            wrapper.like(SysUserOnline::getLoginName, loginName);
        }
        if (StringUtils.hasText(ipaddr)) {
            wrapper.like(SysUserOnline::getIpaddr, ipaddr);
        }
        if (StringUtils.hasText(loginLocation)) {
            wrapper.like(SysUserOnline::getLoginLocation, loginLocation);
        }
        wrapper.orderByDesc(SysUserOnline::getLastAccessTime);
        return this.list(wrapper);
    }

    @Override
    public boolean forceLogout(String sessionId) {
        return this.removeById(sessionId);
    }
}