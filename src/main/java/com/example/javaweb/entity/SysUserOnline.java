package com.example.javaweb.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_user_online")
public class SysUserOnline implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private String sessionId;

    private String loginName;

    private String ipaddr;

    private String loginLocation;

    private String browser;

    private String os;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimestamp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAccessTime;

    private Integer expireTime;
}