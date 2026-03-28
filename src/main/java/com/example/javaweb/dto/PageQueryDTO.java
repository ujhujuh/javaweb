package com.example.javaweb.dto;

import lombok.Data;

@Data
public class PageQueryDTO {
    private Integer current = 1;
    private Integer size = 10;

    public Integer getCurrent() {
        if (current == null || current < 1) {
            return 1;
        }
        return current;
    }

    public Integer getSize() {
        if (size == null || size < 1) {
            return 10;
        }
        return size;
    }
}
