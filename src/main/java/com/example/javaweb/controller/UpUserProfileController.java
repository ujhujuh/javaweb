package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.entity.UpUserProfile;
import com.example.javaweb.service.UpUserProfileService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/userprofile")
public class UpUserProfileController {

    @Autowired
    private UpUserProfileService upUserProfileService;

    @ApiLog("分页查询用户画像")
    @RequiresPermissions("userprofile:list")
    @GetMapping("/list")
    public Result<IPage<UpUserProfile>> list(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              Long userId) {
        Page<UpUserProfile> page = new Page<>(current, size);
        UpUserProfile profile = new UpUserProfile();
        profile.setUserId(userId);
        return Result.success(upUserProfileService.selectList(page, profile));
    }

    @ApiLog("根据ID查询用户画像")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/{id}")
    public Result<UpUserProfile> getById(@PathVariable Long id) {
        return Result.success(upUserProfileService.getById(id));
    }

    @ApiLog("查询用户指定日期的画像")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/byDate")
    public Result<UpUserProfile> getByUserIdAndDate(Long userId,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return Result.success(upUserProfileService.selectByUserIdAndDate(userId, date));
    }

    @ApiLog("查询用户历史画像列表")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/history/{userId}")
    public Result<List<UpUserProfile>> getHistoryByUserId(@PathVariable Long userId) {
        return Result.success(upUserProfileService.selectHistoryByUserId(userId));
    }

    @ApiLog("查询用户日期范围内的画像")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/range")
    public Result<List<UpUserProfile>> getByDateRange(Long userId,
                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(upUserProfileService.selectByDateRange(userId, startDate, endDate));
    }

    @ApiLog("生成用户画像")
    @RequiresPermissions("userprofile:add")
    @PostMapping("/generate")
    public Result<Void> generate(@RequestParam Long userId,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        upUserProfileService.generateUserProfile(userId, date);
        return Result.success();
    }

    @ApiLog("批量生成用户画像")
    @RequiresPermissions("userprofile:add")
    @PostMapping("/generate/batch")
    public Result<Void> batchGenerate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        upUserProfileService.batchGenerateUserProfiles(date);
        return Result.success();
    }

    @ApiLog("删除用户画像")
    @RequiresPermissions("userprofile:remove")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        upUserProfileService.removeById(id);
        return Result.success();
    }
}