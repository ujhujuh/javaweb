package com.example.javaweb.dto;

import lombok.Data;

/**
 * wangEditor上传响应DTO
 */
@Data
public class WangEditorUploadResponseDTO {

    /**
     * 错误码，0 表示成功
     */
    private Integer errno;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 上传的文件数据
     */
    private WangEditorUploadData data;

    /**
     * wangEditor上传数据
     */
    @Data
    public static class WangEditorUploadData {
        /**
         * 图片地址
         */
        private String url;

        /**
         * 图片alt文本
         */
        private String alt;

        /**
         * 图片链接
         */
        private String href;
    }

    /**
     * 创建成功响应
     */
    public static WangEditorUploadResponseDTO success(String url, String fileName) {
        WangEditorUploadResponseDTO response = new WangEditorUploadResponseDTO();
        response.setErrno(0);
        response.setMessage("上传成功");

        WangEditorUploadData data = new WangEditorUploadData();
        data.setUrl(url);
        data.setAlt(fileName);
        data.setHref(url);

        response.setData(data);
        return response;
    }

    /**
     * 创建失败响应
     */
    public static WangEditorUploadResponseDTO failed(String message) {
        WangEditorUploadResponseDTO response = new WangEditorUploadResponseDTO();
        response.setErrno(1);
        response.setMessage(message);
        return response;
    }
}