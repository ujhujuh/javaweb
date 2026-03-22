package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.UpSoftwareUsage;
import com.example.javaweb.mapper.UpSoftwareUsageMapper;
import com.example.javaweb.service.UpSoftwareUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UpSoftwareUsageServiceImpl extends ServiceImpl<UpSoftwareUsageMapper, UpSoftwareUsage> implements UpSoftwareUsageService {

    @Autowired
    private UpSoftwareUsageMapper upSoftwareUsageMapper;

    @Override
    public IPage<UpSoftwareUsage> selectList(IPage<UpSoftwareUsage> page, UpSoftwareUsage usage) {
        LambdaQueryWrapper<UpSoftwareUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(usage.getUserId() != null, UpSoftwareUsage::getUserId, usage.getUserId())
                .like(usage.getSoftwareName() != null, UpSoftwareUsage::getSoftwareName, usage.getSoftwareName())
                .orderByDesc(UpSoftwareUsage::getStartTime);
        return upSoftwareUsageMapper.selectPage(page, wrapper);
    }

    @Override
    public List<UpSoftwareUsage> selectByUserIdAndDate(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        LambdaQueryWrapper<UpSoftwareUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UpSoftwareUsage::getUserId, userId)
                .between(UpSoftwareUsage::getStartTime, startOfDay, endOfDay)
                .orderByDesc(UpSoftwareUsage::getStartTime);
        return upSoftwareUsageMapper.selectList(wrapper);
    }

    @Override
    public List<UpSoftwareUsage> selectByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        LambdaQueryWrapper<UpSoftwareUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UpSoftwareUsage::getUserId, userId)
                .between(UpSoftwareUsage::getStartTime, startDateTime, endDateTime)
                .orderByDesc(UpSoftwareUsage::getStartTime);
        return upSoftwareUsageMapper.selectList(wrapper);
    }
}