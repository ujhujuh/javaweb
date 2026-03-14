package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.SysNotice;

import java.util.List;

public interface SysNoticeService extends IService<SysNotice> {

    IPage<SysNotice> selectNoticeList(IPage<SysNotice> page, SysNotice notice);

    List<SysNotice> selectNoticeList(SysNotice notice);

    SysNotice selectNoticeById(Long noticeId);

    boolean insertNotice(SysNotice notice);

    boolean updateNotice(SysNotice notice);

    boolean deleteNoticeByIds(List<Long> noticeIds);
}