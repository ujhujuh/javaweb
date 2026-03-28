package com.example.javaweb.dto;

import lombok.Data;

@Data
public class UpSoftwareUsageQueryDTO extends PageQueryDTO {
    private Long userId;
    private String softwareName;
}
