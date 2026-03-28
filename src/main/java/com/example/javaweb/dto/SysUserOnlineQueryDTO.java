package com.example.javaweb.dto;

import lombok.Data;

@Data
public class SysUserOnlineQueryDTO {
    private String loginName;
    private String ipaddr;
    private String loginLocation;
}
