package com.example.javaweb.dto;

import lombok.Data;

@Data
public class PortalMaterialListQueryDTO extends PageQueryDTO {
    private String keyword;
    private Long categoryId;
    /**
     * latest/sales/priceAsc/priceDesc
     */
    private String sort = "latest";
}
