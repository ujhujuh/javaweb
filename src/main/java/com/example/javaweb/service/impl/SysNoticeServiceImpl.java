package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.SysNotice;
import com.example.javaweb.mapper.SysNoticeMapper;
import com.example.javaweb.service.SysNoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    @Override
    public IPage<SysNotice> selectNoticeList(IPage<SysNotice> page, SysNotice notice) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        if (notice.getNoticeTitle() != null && !notice.getNoticeTitle().isEmpty()) {
            wrapper.like(SysNotice::getNoticeTitle, notice.getNoticeTitle());
        }
        if (notice.getNoticeType() != null && !notice.getNoticeType().isEmpty()) {
            wrapper.eq(SysNotice::getNoticeType, notice.getNoticeType());
        }
        if (notice.getStatus() != null && !notice.getStatus().isEmpty()) {
            wrapper.eq(SysNotice::getStatus, notice.getStatus());
        }
        wrapper.orderByDesc(SysNotice::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        if (notice.getNoticeTitle() != null && !notice.getNoticeTitle().isEmpty()) {
            wrapper.like(SysNotice::getNoticeTitle, notice.getNoticeTitle());
        }
        if (notice.getNoticeType() != null && !notice.getNoticeType().isEmpty()) {
            wrapper.eq(SysNotice::getNoticeType, notice.getNoticeType());
        }
        if (notice.getStatus() != null && !notice.getStatus().isEmpty()) {
            wrapper.eq(SysNotice::getStatus, notice.getStatus());
        }
        wrapper.orderByDesc(SysNotice::getCreateTime);
        wrapper.last("LIMIT 5");
        return this.list(wrapper);
    }

    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        return this.getById(noticeId);
    }

    @Override
    public boolean insertNotice(SysNotice notice) {
        return this.save(notice);
    }

    @Override
    public boolean updateNotice(SysNotice notice) {
        return this.updateById(notice);
    }

    @Override
    public boolean deleteNoticeByIds(List<Long> noticeIds) {
        return this.removeByIds(noticeIds);
    }
}