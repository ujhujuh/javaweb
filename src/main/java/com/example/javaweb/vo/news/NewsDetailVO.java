package com.example.javaweb.vo.news;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewsDetailVO {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private String tags;
    private String category;
    private Long categoryId;
    private String author;
    private Integer viewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private Boolean locked;
    private Boolean liked;
    private Boolean favorited;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
}
