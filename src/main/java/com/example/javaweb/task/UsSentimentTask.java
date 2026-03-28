package com.example.javaweb.task;

import com.example.javaweb.dto.UsSentimentCollectResultDTO;
import com.example.javaweb.service.UsSentimentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 美股情绪指标收集定时任务
 */
@Component
public class UsSentimentTask {

    private static final Logger logger = LoggerFactory.getLogger(UsSentimentTask.class);

    @Autowired
    private UsSentimentService usSentimentService;

    /**
     * 每天北京时间22:30收集前一个工作日的美股情绪指标数据
     * cron 表达式：每天22:30执行
     */
    @Scheduled(cron = "0 30 22 * * ?")
    public void collectUsSentimentData() {
        try {
            logger.info("开始执行美股情绪指标收集任务");
            UsSentimentCollectResultDTO result = usSentimentService.collectAndSaveSentimentData();
            if (result.isSuccess()) {
                logger.info("美股情绪指标收集任务执行完成: successCount={}, failedCount={}, failedIndicators={}, message={}",
                        result.getSuccessCount(),
                        result.getFailedIndicators().size(),
                        result.getFailedIndicators(),
                        result.getMessage());
            } else {
                logger.error("美股情绪指标收集任务执行失败: successCount={}, failedCount={}, failedIndicators={}, message={}",
                        result.getSuccessCount(),
                        result.getFailedIndicators().size(),
                        result.getFailedIndicators(),
                        result.getMessage());
            }
        } catch (Exception e) {
            logger.error("美股情绪指标收集任务执行异常", e);
        }
    }
}
