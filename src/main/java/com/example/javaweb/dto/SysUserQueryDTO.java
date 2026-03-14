package com.example.javaweb.dto;

import lombok.Data;

@Data
public class SysUserQueryDTO {
    private Integer current = 1;
    private Integer size = 10;
    private String username;
    private String nickname;
    private String phone;
    private String status;
}