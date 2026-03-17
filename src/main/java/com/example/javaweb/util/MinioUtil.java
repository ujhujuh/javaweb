package com.example.javaweb.util;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO文件上传工具类
 */
@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private com.example.javaweb.config.MinioConfig minioConfig;

    /**
     * 判断bucket是否存在，不存在则创建并设置公开策略
     */
    public void existBucket(String name) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());
            }
            // 设置bucket为公开读取
            String policy = "{\n" +
                    "  \"Version\": \"2012-10-17\",\n" +
                    "  \"Statement\": [\n" +
                    "    {\n" +
                    "      \"Effect\": \"Allow\",\n" +
                    "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                    "      \"Action\": [\"s3:GetObject\"],\n" +
                    "      \"Resource\": [\"arn:aws:s3:::" + name + "/*\"]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(name)
                            .config(policy)
                            .build()
            );
        } catch (Exception e) {
            // 记录详细错误信息
            System.err.println("设置bucket策略失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 创建存储bucket
     */
    public void createBucket(String bucketName) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 删除存储bucket
     */
    public void removeBucket(String bucketName) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 文件上传
     *
     * @param file       文件
     * @param bucketName 存储bucket
     * @return 文件访问路径（相对路径）
     */
    public String upload(MultipartFile file, String bucketName) throws Exception {
        // 判断存储桶是否存在，不存在则创建
        existBucket(bucketName);

        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        // 生成新的文件名
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;

        // 上传文件
        InputStream inputStream = file.getInputStream();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        // 返回文件访问路径（相对路径，通过前端代理访问）
        return "/" + bucketName + "/" + fileName;
    }

    /**
     * 文件上传到默认bucket
     *
     * @param file 文件
     * @return 文件访问路径
     */
    public String upload(MultipartFile file) throws Exception {
        return upload(file, minioConfig.getBucketName());
    }

    /**
     * 删除文件
     *
     * @param bucketName 存储bucket
     * @param fileName   文件名称
     */
    public void removeFile(String bucketName, String fileName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    /**
     * 删除文件
     *
     * @param objectName 文件名称（包含bucket）
     */
    public void removeFile(String objectName) throws Exception {
        String[] parts = objectName.split("/");
        if (parts.length >= 2) {
            String bucketName = parts[0];
            String fileName = objectName.substring(bucketName.length() + 1);
            removeFile(bucketName, fileName);
        }
    }

    /**
     * 获取文件访问路径
     *
     * @param bucketName 存储bucket
     * @param fileName   文件名称
     * @return 文件访问路径
     */
    public String getFileUrl(String bucketName, String fileName) throws Exception {
        String endpoint = minioConfig.getEndpoint();
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return endpoint + "/" + bucketName + "/" + fileName;
    }

    /**
     * 获取文件临时访问路径（带签名）
     *
     * @param bucketName 存储bucket
     * @param fileName   文件名称
     * @param expires    过期时间（秒）
     * @return 文件临时访问路径
     */
    public String getPresignedUrl(String bucketName, String fileName, Integer expires) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileName)
                        .expiry(expires, TimeUnit.SECONDS)
                        .build()
        );
    }

    /**
     * 下载文件
     *
     * @param bucketName 存储bucket
     * @param fileName   文件名称
     * @return 文件流
     */
    public InputStream download(String bucketName, String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }
}