package com.example.javaweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("up_user_profile")
public class UpUserProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate profileDate;

    // 基础属性画像
    private String activityLevel;
    private String usagePeriod;
    private String deviceDependency;
    private String workRhythm;

    // 软件使用画像
    private String softwarePreference;
    private Integer softwareDiversity;
    private String professionalLevel;

    // 网络行为画像
    private String browserPreference;
    private String visitCategory;
    private Integer onlineDuration;

    // 统计数据
    private Integer softwareStartCount;
    private Integer fileOperationCount;
    private Integer browserVisitCount;
    private Integer totalUsageDuration;

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