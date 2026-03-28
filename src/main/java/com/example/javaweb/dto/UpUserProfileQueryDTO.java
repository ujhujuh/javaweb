package com.example.javaweb.dto;

import lombok.Data;

@Data
public class UpUserProfileQueryDTO extends PageQueryDTO {
    private Long userId;
}
