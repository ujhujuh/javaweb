package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.UpBrowserBehavior;

import java.time.LocalDate;
import java.util.List;

public interface UpBrowserBehaviorService extends IService<UpBrowserBehavior> {

    IPage<UpBrowserBehavior> selectList(IPage<UpBrowserBehavior> page, UpBrowserBehavior behavior);

    List<UpBrowserBehavior> selectByUserIdAndDate(Long userId, LocalDate date);

    List<UpBrowserBehavior> selectByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);
}