package com.example.javaweb.vo.material;

import lombok.Data;

@Data
public class MaterialCategoryVO {
    private Long id;
    private String name;
    private Long parentId;
    private Long count;
}
