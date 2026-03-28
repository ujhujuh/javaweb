package com.example.javaweb.dto;

import lombok.Data;

/**
 * 美股情绪指标查询DTO
 */
@Data
public class UsSentimentQueryDTO extends PageQueryDTO {

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 满意人数
     */
    private String satisfiedCount;

    /**
     * 是否已发送通知
     */
    private String notificationSent;
}
