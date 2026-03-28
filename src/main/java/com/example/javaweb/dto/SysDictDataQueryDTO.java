package com.example.javaweb.dto;

import lombok.Data;

@Data
public class SysDictDataQueryDTO extends PageQueryDTO {
    private String dictLabel;
    private String dictType;
    private String status;
}
