package com.example.javaweb.vo.news;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewsCommentVO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
