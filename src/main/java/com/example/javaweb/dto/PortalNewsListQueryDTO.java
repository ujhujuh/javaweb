package com.example.javaweb.dto;

import lombok.Data;

@Data
public class PortalNewsListQueryDTO extends PageQueryDTO {
    private String keyword;
    private Long categoryId;
    private String sort = "latest";
}
