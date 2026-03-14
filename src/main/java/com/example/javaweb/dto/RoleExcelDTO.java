package com.example.javaweb.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@ContentRowHeight(20)
@HeadRowHeight(30)
public class RoleExcelDTO {

    @ExcelProperty(value = "角色ID", index = 0)
    @ColumnWidth(15)
    private Long id;

    @ExcelProperty(value = "角色名称", index = 1)
    @ColumnWidth(20)
    private String roleName;

    @ExcelProperty(value = "角色编码", index = 2)
    @ColumnWidth(20)
    private String roleKey;

    @ExcelProperty(value = "显示顺序", index = 3)
    @ColumnWidth(15)
    private Integer roleSort;

    @ExcelProperty(value = "状态", index = 4)
    @ColumnWidth(10)
    private String status;

    @ExcelProperty(value = "创建时间", index = 5)
    @ColumnWidth(25)
    private String createTime;

    @ExcelProperty(value = "备注", index = 6)
    @ColumnWidth(30)
    private String remark;
}