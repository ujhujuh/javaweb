package com.example.javaweb.dto;

import lombok.Data;

@Data
public class MaterialManageListQueryDTO extends PageQueryDTO {
    private String keyword;
    private Long categoryId;
    /**
     * 0草稿 1上架 2下架
     */
    private String status;
}
