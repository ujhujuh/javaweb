package com.example.javaweb.controller;

import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.service.SysUserConfigService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户配置控制器
 */
@RestController
@RequestMapping("/api/system/user/config")
public class SysUserConfigController {

    @Autowired
    private SysUserConfigService sysUserConfigService;

    /**
     * 获取用户配置
     */
    @ApiLog("获取用户配置")
    @RequiresAuthentication
    @GetMapping("/{configKey}")
    public Result<Map<String, String>> getUserConfig(@PathVariable String configKey) {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal instanceof com.example.javaweb.entity.SysUser) {
            com.example.javaweb.entity.SysUser user = (com.example.javaweb.entity.SysUser) principal;
            String value = sysUserConfigService.getUserConfig(user.getId(), configKey);
            Map<String, String> result = new HashMap<>();
            result.put(configKey, value);
            return Result.success(result);
        }
        return Result.failed("未登录");
    }

    /**
     * 设置用户配置
     */
    @ApiLog("设置用户配置")
    @RequiresAuthentication
    @PostMapping
    public Result<Void> setUserConfig(@RequestBody Map<String, String> params) {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal instanceof com.example.javaweb.entity.SysUser) {
            com.example.javaweb.entity.SysUser user = (com.example.javaweb.entity.SysUser) principal;
            String configKey = params.get("configKey");
            String configValue = params.get("configValue");
            sysUserConfigService.setUserConfig(user.getId(), configKey, configValue);
            return Result.success();
        }
        return Result.failed("未登录");
    }

    /**
     * 删除用户配置
     */
    @ApiLog("删除用户配置")
    @RequiresAuthentication
    @DeleteMapping("/{configKey}")
    public Result<Void> deleteUserConfig(@PathVariable String configKey) {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal instanceof com.example.javaweb.entity.SysUser) {
            com.example.javaweb.entity.SysUser user = (com.example.javaweb.entity.SysUser) principal;
            sysUserConfigService.deleteUserConfig(user.getId(), configKey);
            return Result.success();
        }
        return Result.failed("未登录");
    }
}