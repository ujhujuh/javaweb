package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.UpBrowserBehavior;
import com.example.javaweb.mapper.UpBrowserBehaviorMapper;
import com.example.javaweb.service.UpBrowserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UpBrowserBehaviorServiceImpl extends ServiceImpl<UpBrowserBehaviorMapper, UpBrowserBehavior> implements UpBrowserBehaviorService {

    @Autowired
    private UpBrowserBehaviorMapper upBrowserBehaviorMapper;

    @Override
    public IPage<UpBrowserBehavior> selectList(IPage<UpBrowserBehavior> page, UpBrowserBehavior behavior) {
        LambdaQueryWrapper<UpBrowserBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(behavior.getUserId() != null, UpBrowserBehavior::getUserId, behavior.getUserId())
                .like(behavior.getUrl() != null, UpBrowserBehavior::getUrl, behavior.getUrl())
                .eq(behavior.getBrowserType() != null, UpBrowserBehavior::getBrowserType, behavior.getBrowserType())
                .orderByDesc(UpBrowserBehavior::getVisitTime);
        return upBrowserBehaviorMapper.selectPage(page, wrapper);
    }

    @Override
    public List<UpBrowserBehavior> selectByUserIdAndDate(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        LambdaQueryWrapper<UpBrowserBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UpBrowserBehavior::getUserId, userId)
                .between(UpBrowserBehavior::getVisitTime, startOfDay, endOfDay)
                .orderByDesc(UpBrowserBehavior::getVisitTime);
        return upBrowserBehaviorMapper.selectList(wrapper);
    }

    @Override
    public List<UpBrowserBehavior> selectByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        LambdaQueryWrapper<UpBrowserBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UpBrowserBehavior::getUserId, userId)
                .between(UpBrowserBehavior::getVisitTime, startDateTime, endDateTime)
                .orderByDesc(UpBrowserBehavior::getVisitTime);
        return upBrowserBehaviorMapper.selectList(wrapper);
    }
}