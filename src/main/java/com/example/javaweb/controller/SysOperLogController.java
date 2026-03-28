package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.dto.SysOperLogQueryDTO;
import com.example.javaweb.entity.SysOperLog;
import com.example.javaweb.service.SysOperLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/operlog")
public class SysOperLogController {

    @Autowired
    private SysOperLogService sysOperLogService;

    @ApiLog("分页查询操作日志")
    @RequiresPermissions("system:operlog:list")
    @GetMapping("/list")
    public Result<IPage<SysOperLog>> list(SysOperLogQueryDTO queryDTO) {
        Page<SysOperLog> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        SysOperLog operLog = new SysOperLog();
        operLog.setTitle(queryDTO.getTitle());
        operLog.setOperName(queryDTO.getOperName());
        operLog.setOperIp(queryDTO.getOperIp());
        operLog.setStatus(queryDTO.getStatus());
        return Result.success(sysOperLogService.selectOperLogList(page, operLog));
    }

    @ApiLog("根据ID查询操作日志")
    @RequiresPermissions("system:operlog:query")
    @GetMapping("/{id}")
    public Result<SysOperLog> getById(@PathVariable Long id) {
        return Result.success(sysOperLogService.getById(id));
    }

    @ApiLog("删除操作日志")
    @RequiresPermissions("system:operlog:remove")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        sysOperLogService.deleteOperLogByIds(ids);
        return Result.success();
    }

    @ApiLog("清空操作日志")
    @RequiresPermissions("system:operlog:remove")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        sysOperLogService.cleanOperLog();
        return Result.success();
    }
}
