package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.UsSentiment;

import java.util.List;

public interface UsSentimentService extends IService<UsSentiment> {

    IPage<UsSentiment> selectUsSentimentList(IPage<UsSentiment> page, UsSentiment usSentiment);

    List<UsSentiment> selectUsSentimentList(UsSentiment usSentiment);

    IPage<UsSentiment> selectUsSentimentList(IPage<UsSentiment> page, UsSentiment usSentiment, String startDate, String endDate, String satisfiedCount, String notificationSent);

    UsSentiment selectUsSentimentById(Long id);

    boolean insertUsSentiment(UsSentiment usSentiment);

    boolean updateUsSentiment(UsSentiment usSentiment);

    boolean deleteUsSentimentByIds(List<Long> ids);

    boolean collectAndSaveSentimentData();

    boolean checkConditionsAndSendNotification(UsSentiment usSentiment);
}