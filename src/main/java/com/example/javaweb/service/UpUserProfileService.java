package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.UpUserProfile;

import java.time.LocalDate;
import java.util.List;

public interface UpUserProfileService extends IService<UpUserProfile> {

    IPage<UpUserProfile> selectList(IPage<UpUserProfile> page, UpUserProfile profile);

    UpUserProfile selectByUserIdAndDate(Long userId, LocalDate date);

    List<UpUserProfile> selectHistoryByUserId(Long userId);

    List<UpUserProfile> selectByDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 生成用户画像
     */
    boolean generateUserProfile(Long userId, LocalDate date);

    /**
     * 批量生成用户画像（定时任务）
     */
    void batchGenerateUserProfiles(LocalDate date);
}