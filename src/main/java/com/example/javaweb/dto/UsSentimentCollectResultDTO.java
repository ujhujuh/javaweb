package com.example.javaweb.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class UsSentimentCollectResultDTO {

    private boolean success;

    private boolean saved;

    private int successCount;

    private Map<String, String> failedIndicators = new LinkedHashMap<>();

    private String message;
}
