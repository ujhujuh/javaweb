package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.UpSoftwareUsage;

import java.time.LocalDate;
import java.util.List;

public interface UpSoftwareUsageService extends IService<UpSoftwareUsage> {

    IPage<UpSoftwareUsage> selectList(IPage<UpSoftwareUsage> page, UpSoftwareUsage usage);

    List<UpSoftwareUsage> selectByUserIdAndDate(Long userId, LocalDate date);

    List<UpSoftwareUsage> selectByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);
}