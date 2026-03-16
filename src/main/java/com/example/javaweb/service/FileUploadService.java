package com.example.javaweb.service;

import com.example.javaweb.dto.FileUploadDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传Service
 */
public interface FileUploadService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件上传结果
     */
    FileUploadDTO upload(MultipartFile file);

    /**
     * 上传文件到指定bucket
     *
     * @param file       文件
     * @param bucketName 存储桶名称
     * @return 文件上传结果
     */
    FileUploadDTO upload(MultipartFile file, String bucketName);

    /**
     * 删除文件
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名称
     */
    void delete(String bucketName, String fileName);
}