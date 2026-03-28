package com.example.javaweb.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MaterialCategorySaveDTO {
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100")
    private String categoryName;

    @NotNull(message = "父级分类不能为空")
    private Long parentId;

    @NotNull(message = "排序值不能为空")
    private Integer sortNum;

    /**
     * 0正常 1禁用
     */
    private String status;
}
