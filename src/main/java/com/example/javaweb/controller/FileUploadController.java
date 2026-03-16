package com.example.javaweb.controller;

import com.example.javaweb.common.exception.FileUploadException;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.dto.FileDeleteRequestDTO;
import com.example.javaweb.dto.FileUploadDTO;
import com.example.javaweb.dto.FileUploadRequestDTO;
import com.example.javaweb.dto.WangEditorUploadResponseDTO;
import com.example.javaweb.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 文件上传Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@Validated
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 上传图片（用于富文本编辑器）
     * wangEditor需要的响应格式：{ errno: 0, data: { url: 'xxx', alt: 'xxx', href: 'xxx' } }
     *
     * @param file 文件
     * @return 上传结果
     */
    @ApiLog("上传图片")
    @PostMapping("/upload/image")
    public WangEditorUploadResponseDTO uploadImage(@NotNull(message = "文件不能为空") @RequestParam("file") MultipartFile file) {
        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileUploadException("只支持图片文件");
        }

        // 校验文件大小（10MB）
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new FileUploadException("文件大小不能超过10MB");
        }

        // 上传文件
        FileUploadDTO uploadDTO = fileUploadService.upload(file);

        // 构建wangEditor需要的响应格式
        return WangEditorUploadResponseDTO.success(uploadDTO.getUrl(), uploadDTO.getFileName());
    }

    /**
     * 通用文件上传
     *
     * @param request 文件上传请求
     * @return 上传结果
     */
    @ApiLog("上传文件")
    @RequiresPermissions("system:file:upload")
    @PostMapping("/upload")
    public Result<FileUploadDTO> upload(@Valid @RequestBody FileUploadRequestDTO request) {
        // 上传文件
        FileUploadDTO uploadDTO = request.getBucketName() != null
                ? fileUploadService.upload(request.getFile(), request.getBucketName())
                : fileUploadService.upload(request.getFile());

        return Result.success(uploadDTO);
    }

    /**
     * 删除文件
     *
     * @param request 文件删除请求
     * @return 删除结果
     */
    @ApiLog("删除文件")
    @RequiresPermissions("system:file:delete")
    @DeleteMapping("/delete")
    public Result<Void> delete(@Valid @RequestBody FileDeleteRequestDTO request) {
        fileUploadService.delete(request.getBucketName(), request.getFileName());
        return Result.success();
    }
}