package com.example.javaweb.vo.material;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MaterialCreateOrderVO {
    private String orderNo;
    private Long materialId;
    private String materialTitle;
    private BigDecimal payAmount;
    private List<String> payMethods;
}
