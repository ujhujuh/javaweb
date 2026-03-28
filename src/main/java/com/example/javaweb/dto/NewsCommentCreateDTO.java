package com.example.javaweb.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewsCommentCreateDTO {

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论长度不能超过1000")
    private String content;
}
