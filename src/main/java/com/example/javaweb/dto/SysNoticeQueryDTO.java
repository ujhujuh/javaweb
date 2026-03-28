package com.example.javaweb.dto;

import lombok.Data;

@Data
public class SysNoticeQueryDTO extends PageQueryDTO {
    private String noticeTitle;
    private String noticeType;
    private String status;
}
