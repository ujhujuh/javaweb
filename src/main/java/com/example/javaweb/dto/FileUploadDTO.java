package com.example.javaweb.dto;

import lombok.Data;

/**
 * 文件上传响应DTO
 */
@Data
public class FileUploadDTO {

    /**
     * 文件访问路径
     */
    private String url;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 存储桶名称
     */
    private String bucketName;
}