package com.example.javaweb.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * 文件上传请求DTO
 */
@Data
public class FileUploadRequestDTO {

    /**
     * 上传的文件
     */
    @NotNull(message = "文件不能为空")
    private MultipartFile file;

    /**
     * 存储桶名称（可选）
     */
    private String bucketName;
}