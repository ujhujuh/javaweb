package com.example.javaweb.controller;

import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.dto.SysUserOnlineQueryDTO;
import com.example.javaweb.entity.SysUserOnline;
import com.example.javaweb.service.SysUserOnlineService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/online")
public class SysUserOnlineController {

    @Autowired
    private SysUserOnlineService sysUserOnlineService;

    @ApiLog("查询在线用户")
    @RequiresPermissions("system:online:list")
    @GetMapping("/list")
    public Result<List<SysUserOnline>> list(SysUserOnlineQueryDTO queryDTO) {
        return Result.success(sysUserOnlineService.selectOnlineList(
                queryDTO.getLoginName(),
                queryDTO.getIpaddr(),
                queryDTO.getLoginLocation()
        ));
    }

    @ApiLog("强退用户")
    @RequiresPermissions("system:online:forceLogout")
    @DeleteMapping("/forceLogout/{sessionId}")
    public Result<Void> forceLogout(@PathVariable String sessionId) {
        sysUserOnlineService.forceLogout(sessionId);
        return Result.success();
    }
}
