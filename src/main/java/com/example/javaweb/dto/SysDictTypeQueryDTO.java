package com.example.javaweb.dto;

import lombok.Data;

@Data
public class SysDictTypeQueryDTO extends PageQueryDTO {
    private String dictName;
    private String dictType;
    private String status;
}
