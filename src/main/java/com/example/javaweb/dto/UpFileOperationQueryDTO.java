package com.example.javaweb.dto;

import lombok.Data;

@Data
public class UpFileOperationQueryDTO extends PageQueryDTO {
    private Long userId;
    private String operationType;
    private String fileType;
}
