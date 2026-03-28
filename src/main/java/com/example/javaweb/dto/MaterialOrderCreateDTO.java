package com.example.javaweb.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MaterialOrderCreateDTO {

    @NotNull(message = "资料ID不能为空")
    private Long materialId;
}
