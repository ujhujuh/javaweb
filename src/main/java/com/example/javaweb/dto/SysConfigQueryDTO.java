package com.example.javaweb.dto;

import lombok.Data;

@Data
public class SysConfigQueryDTO extends PageQueryDTO {
    private String configName;
    private String configKey;
    private String configType;
}
