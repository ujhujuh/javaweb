package com.example.javaweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("us_sentiment")
public class UsSentiment implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private BigDecimal vix;

    private BigDecimal fearGreed;

    private BigDecimal naaim;

    private BigDecimal rsiSp500;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    private String vixCondition;

    private String fearGreedCondition;

    private String naaimCondition;

    private String rsiCondition;

    private Integer satisfiedCount;

    private String notificationSent;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String remark;
}