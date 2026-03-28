package com.example.javaweb.vo.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MaterialOrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String username;
    private Long materialId;
    private String materialTitle;
    private String materialCover;
    private BigDecimal payAmount;
    private String payType;
    private String status;
    private Integer downloadCount;
    private Integer maxDownloadCount;
    private String fileUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliverTime;
}
