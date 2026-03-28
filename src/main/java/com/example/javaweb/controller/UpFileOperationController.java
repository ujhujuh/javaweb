package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.dto.UpFileOperationQueryDTO;
import com.example.javaweb.entity.UpFileOperation;
import com.example.javaweb.service.UpFileOperationService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/userprofile/file")
public class UpFileOperationController {

    @Autowired
    private UpFileOperationService upFileOperationService;

    @ApiLog("分页查询文件操作记录")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/list")
    public Result<IPage<UpFileOperation>> list(UpFileOperationQueryDTO queryDTO) {
        Page<UpFileOperation> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        UpFileOperation operation = new UpFileOperation();
        operation.setUserId(queryDTO.getUserId());
        operation.setOperationType(queryDTO.getOperationType());
        operation.setFileType(queryDTO.getFileType());
        return Result.success(upFileOperationService.selectList(page, operation));
    }

    @ApiLog("根据ID查询文件操作记录")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/{id}")
    public Result<UpFileOperation> getById(@PathVariable Long id) {
        return Result.success(upFileOperationService.getById(id));
    }

    @ApiLog("新增文件操作记录")
    @PostMapping
    public Result<Void> add(@RequestBody UpFileOperation operation) {
        upFileOperationService.save(operation);
        return Result.success();
    }

    @ApiLog("修改文件操作记录")
    @RequiresPermissions("userprofile:edit")
    @PutMapping
    public Result<Void> update(@RequestBody UpFileOperation operation) {
        upFileOperationService.updateById(operation);
        return Result.success();
    }

    @ApiLog("删除文件操作记录")
    @RequiresPermissions("userprofile:remove")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        upFileOperationService.removeById(id);
        return Result.success();
    }

    @ApiLog("查询用户指定日期的文件操作记录")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/byDate")
    public Result<List<UpFileOperation>> getByUserIdAndDate(Long userId, String date) {
        LocalDate localDate = LocalDate.parse(date);
        return Result.success(upFileOperationService.selectByUserIdAndDate(userId, localDate));
    }
}
