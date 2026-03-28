package com.example.javaweb.vo.news;

import lombok.Data;

@Data
public class NewsCategoryVO {
    private Long id;
    private String name;
    private Long parentId;
    private Long count;
}
