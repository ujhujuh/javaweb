package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.entity.UpBrowserBehavior;
import com.example.javaweb.service.UpBrowserBehaviorService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/userprofile/browser")
public class UpBrowserBehaviorController {

    @Autowired
    private UpBrowserBehaviorService upBrowserBehaviorService;

    @ApiLog("分页查询浏览器行为记录")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/list")
    public Result<IPage<UpBrowserBehavior>> list(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  Long userId, String url, String browserType) {
        Page<UpBrowserBehavior> page = new Page<>(current, size);
        UpBrowserBehavior behavior = new UpBrowserBehavior();
        behavior.setUserId(userId);
        behavior.setUrl(url);
        behavior.setBrowserType(browserType);
        return Result.success(upBrowserBehaviorService.selectList(page, behavior));
    }

    @ApiLog("根据ID查询浏览器行为记录")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/{id}")
    public Result<UpBrowserBehavior> getById(@PathVariable Long id) {
        return Result.success(upBrowserBehaviorService.getById(id));
    }

    @ApiLog("新增浏览器行为记录")
    @PostMapping
    public Result<Void> add(@RequestBody UpBrowserBehavior behavior) {
        upBrowserBehaviorService.save(behavior);
        return Result.success();
    }

    @ApiLog("修改浏览器行为记录")
    @RequiresPermissions("userprofile:edit")
    @PutMapping
    public Result<Void> update(@RequestBody UpBrowserBehavior behavior) {
        upBrowserBehaviorService.updateById(behavior);
        return Result.success();
    }

    @ApiLog("删除浏览器行为记录")
    @RequiresPermissions("userprofile:remove")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        upBrowserBehaviorService.removeById(id);
        return Result.success();
    }

    @ApiLog("查询用户指定日期的浏览器行为记录")
    @RequiresPermissions("userprofile:query")
    @GetMapping("/byDate")
    public Result<List<UpBrowserBehavior>> getByUserIdAndDate(Long userId, String date) {
        LocalDate localDate = LocalDate.parse(date);
        return Result.success(upBrowserBehaviorService.selectByUserIdAndDate(userId, localDate));
    }
}