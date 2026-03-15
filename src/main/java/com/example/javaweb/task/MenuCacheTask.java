package com.example.javaweb.task;

import com.example.javaweb.service.SysMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 菜单缓存定时任务
 */
@Component
public class MenuCacheTask {

    private static final Logger logger = LoggerFactory.getLogger(MenuCacheTask.class);

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 每小时刷新所有用户菜单缓存
     * cron 表达式：每小时的第 0 分钟执行
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void refreshAllUserMenuCache() {
        try {
            logger.info("开始执行菜单缓存刷新任务");
            sysMenuService.refreshAllUserMenuCache();
            logger.info("菜单缓存刷新任务执行成功");
        } catch (Exception e) {
            logger.error("菜单缓存刷新任务执行失败", e);
        }
    }
}