package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.entity.UpSoftwareUsage;
import com.example.javaweb.service.UpSoftwareUsageService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/userprofile/software")
public class UpSoftwareUsageController {

    @Autowired
    private UpSoftwareUsageService upSoftwareUsageService;

    @ApiLog("分页查询软件使用记录")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/list")
    public Result<IPage<UpSoftwareUsage>> list(@RequestParam(defaultValue = "1") Integer current,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                Long userId, String softwareName) {
        Page<UpSoftwareUsage> page = new Page<>(current, size);
        UpSoftwareUsage usage = new UpSoftwareUsage();
        usage.setUserId(userId);
        usage.setSoftwareName(softwareName);
        return Result.success(upSoftwareUsageService.selectList(page, usage));
    }

    @ApiLog("根据ID查询软件使用记录")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/{id}")
    public Result<UpSoftwareUsage> getById(@PathVariable Long id) {
        return Result.success(upSoftwareUsageService.getById(id));
    }

    @ApiLog("新增软件使用记录")
    @RequiresPermissions("userprofile:add")
    @PostMapping
    public Result<Void> add(@RequestBody UpSoftwareUsage usage) {
        upSoftwareUsageService.save(usage);
        return Result.success();
    }

    @ApiLog("修改软件使用记录")
    @RequiresPermissions("userprofile:edit")
    @PutMapping
    public Result<Void> update(@RequestBody UpSoftwareUsage usage) {
        upSoftwareUsageService.updateById(usage);
        return Result.success();
    }

    @ApiLog("删除软件使用记录")
    @RequiresPermissions("userprofile:remove")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        upSoftwareUsageService.removeById(id);
        return Result.success();
    }

    @ApiLog("查询用户指定日期的软件使用记录")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/byDate")
    public Result<List<UpSoftwareUsage>> getByUserIdAndDate(Long userId, String date) {
        LocalDate localDate = LocalDate.parse(date);
        return Result.success(upSoftwareUsageService.selectByUserIdAndDate(userId, localDate));
    }
}