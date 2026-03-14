package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.SysUserNotice;
import com.example.javaweb.mapper.SysUserNoticeMapper;
import com.example.javaweb.service.SysUserNoticeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SysUserNoticeServiceImpl extends ServiceImpl<SysUserNoticeMapper, SysUserNotice> implements SysUserNoticeService {

    @Override
    public boolean markAsRead(Long userId, Long noticeId) {
        LambdaQueryWrapper<SysUserNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserNotice::getUserId, userId)
                .eq(SysUserNotice::getNoticeId, noticeId);
        SysUserNotice userNotice = getOne(wrapper);
        
        if (userNotice == null) {
            userNotice = new SysUserNotice();
            userNotice.setUserId(userId);
            userNotice.setNoticeId(noticeId);
        }
        
        userNotice.setReadStatus("1");
        userNotice.setReadTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return saveOrUpdate(userNotice);
    }

    @Override
    public SysUserNotice getUserNotice(Long userId, Long noticeId) {
        LambdaQueryWrapper<SysUserNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserNotice::getUserId, userId)
                .eq(SysUserNotice::getNoticeId, noticeId);
        return getOne(wrapper);
    }
}