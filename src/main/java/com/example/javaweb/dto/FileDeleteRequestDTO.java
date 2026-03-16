package com.example.javaweb.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 文件删除请求DTO
 */
@Data
public class FileDeleteRequestDTO {

    /**
     * 存储桶名称
     */
    @NotBlank(message = "存储桶名称不能为空")
    private String bucketName;

    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空")
    private String fileName;
}