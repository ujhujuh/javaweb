package com.example.javaweb.service.impl;

import com.example.javaweb.common.exception.FileUploadException;
import com.example.javaweb.config.MinioConfig;
import com.example.javaweb.dto.FileUploadDTO;
import com.example.javaweb.service.FileUploadService;
import com.example.javaweb.util.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传Service实现
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MinioConfig minioConfig;

    @Override
    public FileUploadDTO upload(MultipartFile file) {
        return upload(file, minioConfig.getBucketName());
    }

    @Override
    public FileUploadDTO upload(MultipartFile file, String bucketName) {
        try {
            // 上传文件到MinIO
            String fileUrl = minioUtil.upload(file, bucketName);

            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            String fileName = originalFilename;

            // 构建返回结果
            FileUploadDTO dto = new FileUploadDTO();
            dto.setUrl(fileUrl);
            dto.setFileName(fileName);
            dto.setFileSize(file.getSize());
            dto.setFileType(file.getContentType());
            dto.setBucketName(bucketName);

            return dto;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new FileUploadException("文件上传失败：" + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String bucketName, String fileName) {
        try {
            minioUtil.removeFile(bucketName, fileName);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new FileUploadException("文件删除失败：" + e.getMessage(), e);
        }
    }
}