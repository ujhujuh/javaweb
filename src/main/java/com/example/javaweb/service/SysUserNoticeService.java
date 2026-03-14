package com.example.javaweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.SysUserNotice;

public interface SysUserNoticeService extends IService<SysUserNotice> {
    boolean markAsRead(Long userId, Long noticeId);
    SysUserNotice getUserNotice(Long userId, Long noticeId);
}