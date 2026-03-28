package com.example.javaweb.dto;

import lombok.Data;

@Data
public class SysLoginLogQueryDTO extends PageQueryDTO {
    private String username;
    private String ipaddr;
    private String status;
}
