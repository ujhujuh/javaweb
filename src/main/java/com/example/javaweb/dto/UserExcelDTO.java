package com.example.javaweb.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@ContentRowHeight(20)
@HeadRowHeight(30)
public class UserExcelDTO {

    @ExcelProperty(value = "用户ID", index = 0)
    @ColumnWidth(15)
    private Long id;

    @ExcelProperty(value = "用户名", index = 1)
    @ColumnWidth(20)
    private String username;

    @ExcelProperty(value = "昵称", index = 2)
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(value = "邮箱", index = 3)
    @ColumnWidth(25)
    private String email;

    @ExcelProperty(value = "手机号", index = 4)
    @ColumnWidth(20)
    private String phone;

    @ExcelProperty(value = "性别", index = 5)
    @ColumnWidth(10)
    private String sex;

    @ExcelProperty(value = "状态", index = 6)
    @ColumnWidth(10)
    private String status;

    @ExcelProperty(value = "创建时间", index = 7)
    @ColumnWidth(25)
    private String createTime;

    @ExcelProperty(value = "备注", index = 8)
    @ColumnWidth(30)
    private String remark;
}