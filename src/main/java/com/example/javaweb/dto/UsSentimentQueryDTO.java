package com.example.javaweb.dto;

import lombok.Data;

/**
 * 美股情绪指标查询DTO
 */
@Data
public class UsSentimentQueryDTO {

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

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