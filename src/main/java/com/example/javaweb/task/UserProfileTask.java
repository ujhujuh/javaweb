package com.example.javaweb.task;

import com.example.javaweb.service.UpUserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 用户画像定时任务
 */
@Component
public class UserProfileTask {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileTask.class);

    @Autowired
    private UpUserProfileService upUserProfileService;

    /**
     * 每日凌晨1点生成所有用户的画像
     * cron 表达式：每天 01:00:00 执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateAllUserProfiles() {
        try {
            logger.info("开始执行用户画像生成任务");
            // 生成昨天的画像
            LocalDate yesterday = LocalDate.now().minusDays(1);
            upUserProfileService.batchGenerateUserProfiles(yesterday);
            logger.info("用户画像生成任务执行成功");
        } catch (Exception e) {
            logger.error("用户画像生成任务执行失败", e);
        }
    }
}