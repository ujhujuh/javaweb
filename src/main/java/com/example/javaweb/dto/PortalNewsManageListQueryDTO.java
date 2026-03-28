package com.example.javaweb.dto;

import lombok.Data;

@Data
public class PortalNewsManageListQueryDTO extends PageQueryDTO {
    private String keyword;
    private Long categoryId;
    private String status;
}
