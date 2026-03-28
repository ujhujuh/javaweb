package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.context.CurrentUserContext;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.dto.SysNoticeQueryDTO;
import com.example.javaweb.entity.SysNotice;
import com.example.javaweb.entity.SysUser;
import com.example.javaweb.entity.SysUserNotice;
import com.example.javaweb.service.SysNoticeService;
import com.example.javaweb.service.SysUserNoticeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/system/notice")
public class SysNoticeController {

    @Autowired
    private SysNoticeService sysNoticeService;

    @Autowired
    private SysUserNoticeService sysUserNoticeService;

    @ApiLog("分页查询公告")
    @RequiresPermissions("system:notice:list")
    @GetMapping("/list")
    public Result<IPage<SysNotice>> list(SysNoticeQueryDTO queryDTO) {
        Page<SysNotice> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        SysNotice notice = new SysNotice();
        notice.setNoticeTitle(queryDTO.getNoticeTitle());
        notice.setNoticeType(queryDTO.getNoticeType());
        notice.setStatus(queryDTO.getStatus());
        return Result.success(sysNoticeService.selectNoticeList(page, notice));
    }

    @ApiLog("查询最新公告")
    @GetMapping("/latest")
    public Result<List<SysNotice>> latest() {
        SysNotice notice = new SysNotice();
        notice.setStatus("0");
        return Result.success(sysNoticeService.selectNoticeList(notice));
    }

    @ApiLog("查询用户公告状态")
    @GetMapping("/user/status")
    public Result<List<SysUserNotice>> userNoticeStatus() {
        SysUser user = CurrentUserContext.getUser();
        if (user == null) {
            return Result.failed("未登录或登录已过期");
        }
        return Result.success(sysUserNoticeService.lambdaQuery().eq(SysUserNotice::getUserId, user.getId()).list());
    }

    @ApiLog("标记公告为已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        SysUser user = CurrentUserContext.getUser();
        if (user == null) {
            return Result.failed("未登录或登录已过期");
        }
        sysUserNoticeService.markAsRead(user.getId(), id);
        return Result.success();
    }

    @ApiLog("根据ID查询公告")
    @GetMapping("/{id}")
    public Result<SysNotice> getById(@PathVariable Long id) {
        return Result.success(sysNoticeService.selectNoticeById(id));
    }

    @ApiLog("新增公告")
    @RequiresPermissions("system:notice:add")
    @PostMapping
    public Result<Void> add(@RequestBody SysNotice notice) {
        sysNoticeService.insertNotice(notice);
        return Result.success();
    }

    @ApiLog("修改公告")
    @RequiresPermissions("system:notice:edit")
    @PutMapping
    public Result<Void> update(@RequestBody SysNotice notice) {
        sysNoticeService.updateNotice(notice);
        return Result.success();
    }

    @ApiLog("删除公告")
    @RequiresPermissions("system:notice:remove")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        sysNoticeService.deleteNoticeByIds(Arrays.asList(ids));
        return Result.success();
    }
}
