package com.example.javaweb.dto;

import lombok.Data;

@Data
public class MaterialOrderQueryDTO extends PageQueryDTO {
    /**
     * PENDING/PAID/COMPLETED/CLOSED
     */
    private String status;
}
