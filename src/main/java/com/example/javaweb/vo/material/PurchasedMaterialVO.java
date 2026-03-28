package com.example.javaweb.vo.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PurchasedMaterialVO {
    private String orderNo;
    private Long materialId;
    private String materialTitle;
    private String materialCover;
    private String fileUrl;
    private Integer downloadCount;
    private Integer maxDownloadCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime purchaseTime;
}
