package com.example.javaweb.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class MaterialPublishDTO {

    @NotBlank(message = "资料标题不能为空")
    @Size(max = 200, message = "资料标题长度不能超过200")
    private String title;

    @Size(max = 1000, message = "资料简介长度不能超过1000")
    private String summary;

    @NotBlank(message = "资料详情不能为空")
    private String detailContent;

    private String coverImage;

    private String previewUrl;

    @NotBlank(message = "资料文件地址不能为空")
    private String fileUrl;

    @Size(max = 100, message = "文件格式长度不能超过100")
    private String fileFormat;

    @Size(max = 50, message = "文件大小长度不能超过50")
    private String fileSize;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0", message = "价格不能小于0")
    private BigDecimal price;

    private String tags;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /**
     * 0草稿 1上架 2下架
     */
    private String status;

    @NotNull(message = "最大下载次数不能为空")
    private Integer maxDownloadCount;

    /**
     * 0付费 1免费
     */
    private String isFree;
}
