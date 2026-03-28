package com.example.javaweb.dto;

import lombok.Data;

@Data
public class SysOperLogQueryDTO extends PageQueryDTO {
    private String title;
    private String operName;
    private String operIp;
    private Integer status;
}
