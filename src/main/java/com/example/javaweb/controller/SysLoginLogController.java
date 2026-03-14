package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.entity.SysLoginLog;
import com.example.javaweb.service.SysLoginLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/loginlog")
public class SysLoginLogController {

    @Autowired
    private SysLoginLogService sysLoginLogService;

    @ApiLog("分页查询登录日志")
    @RequiresPermissions("system:loginlog:list")
    @GetMapping("/list")
    public Result<IPage<SysLoginLog>> list(@RequestParam(defaultValue = "1") Integer current,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) String username,
                                          @RequestParam(required = false) String ipaddr,
                                          @RequestParam(required = false) String status) {
        Page<SysLoginLog> page = new Page<>(current, size);
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setUsername(username);
        loginLog.setIpaddr(ipaddr);
        loginLog.setStatus(status);
        return Result.success(sysLoginLogService.selectLoginLogList(page, loginLog));
    }

    @ApiLog("根据ID查询登录日志")
    @RequiresPermissions("system:loginlog:query")
    @GetMapping("/{id}")
    public Result<SysLoginLog> getById(@PathVariable Long id) {
        return Result.success(sysLoginLogService.getById(id));
    }

    @ApiLog("删除登录日志")
    @RequiresPermissions("system:loginlog:remove")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        sysLoginLogService.deleteLoginLogByIds(ids);
        return Result.success();
    }

    @ApiLog("清空登录日志")
    @RequiresPermissions("system:loginlog:remove")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        sysLoginLogService.cleanLoginLog();
        return Result.success();
    }
}