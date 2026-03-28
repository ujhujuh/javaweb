package com.example.javaweb.dto;

import lombok.Data;

@Data
public class UpBrowserBehaviorQueryDTO extends PageQueryDTO {
    private Long userId;
    private String url;
    private String browserType;
}
