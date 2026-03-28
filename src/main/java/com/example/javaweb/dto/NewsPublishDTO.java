package com.example.javaweb.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewsPublishDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200")
    private String title;

    @Size(max = 1000, message = "摘要长度不能超过1000")
    private String summary;

    @NotBlank(message = "正文不能为空")
    private String content;

    private String coverImage;

    private String tags;

    private Long categoryId;

    /**
     * 0公开 1登录可见
     */
    private String visibleScope;

    /**
     * 0草稿 1发布
     */
    private String status;
}
