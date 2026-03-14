package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.dto.SysConfigQueryDTO;
import com.example.javaweb.entity.SysConfig;
import com.example.javaweb.service.SysConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/system/config")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @ApiLog("分页查询参数配置")
    @RequiresPermissions("system:config:list")
    @GetMapping("/list")
    public Result<IPage<SysConfig>> list(SysConfigQueryDTO queryDTO) {
        Page<SysConfig> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        SysConfig config = new SysConfig();
        config.setConfigName(queryDTO.getConfigName());
        config.setConfigKey(queryDTO.getConfigKey());
        config.setConfigType(queryDTO.getConfigType());
        return Result.success(sysConfigService.selectConfigList(page, config));
    }

    @ApiLog("根据ID查询参数配置")
    @RequiresPermissions("system:config:query")
    @GetMapping("/{id}")
    public Result<SysConfig> getById(@PathVariable Long id) {
        return Result.success(sysConfigService.selectConfigById(id));
    }

    @ApiLog("新增参数配置")
    @RequiresPermissions("system:config:add")
    @PostMapping
    public Result<Void> add(@RequestBody SysConfig config) {
        if (!sysConfigService.checkConfigKeyUnique(config)) {
            return Result.failed("参数键名已存在");
        }
        sysConfigService.insertConfig(config);
        return Result.success();
    }

    @ApiLog("修改参数配置")
    @RequiresPermissions("system:config:edit")
    @PutMapping
    public Result<Void> update(@RequestBody SysConfig config) {
        if (!sysConfigService.checkConfigKeyUnique(config)) {
            return Result.failed("参数键名已存在");
        }
        sysConfigService.updateConfig(config);
        return Result.success();
    }

    @ApiLog("删除参数配置")
    @RequiresPermissions("system:config:remove")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        sysConfigService.deleteConfigByIds(Arrays.asList(ids));
        return Result.success();
    }

    @ApiLog("刷新参数缓存")
    @RequiresPermissions("system:config:refresh")
    @DeleteMapping("/refresh")
    public Result<Void> refresh() {
        return Result.success();
    }
}