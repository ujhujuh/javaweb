package com.example.javaweb.dto;

import lombok.Data;

@Data
public class SysRoleQueryDTO extends PageQueryDTO {
    private String roleName;
    private String roleKey;
    private String status;
}
