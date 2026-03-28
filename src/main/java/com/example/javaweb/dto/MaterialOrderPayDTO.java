package com.example.javaweb.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MaterialOrderPayDTO {
    @NotBlank(message = "支付方式不能为空")
    private String payType;
}
