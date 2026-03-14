package com.example.javaweb.dto;

import lombok.Data;

@Data
public class SysConfigQueryDTO {
    private Integer current = 1;
    private Integer size = 10;
    private String configName;
    private String configKey;
    private String configType;
}