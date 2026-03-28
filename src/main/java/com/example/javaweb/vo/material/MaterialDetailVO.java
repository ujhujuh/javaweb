package com.example.javaweb.vo.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MaterialDetailVO {
    private Long id;
    private String title;
    private String summary;
    private String detailContent;
    private String coverImage;
    private String previewUrl;
    private String fileFormat;
    private String fileSize;
    private String tags;
    private Long categoryId;
    private String category;
    private BigDecimal price;
    private Boolean free;
    private Integer salesCount;
    private Integer maxDownloadCount;
    private Boolean purchased;
    private String fileUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
}
