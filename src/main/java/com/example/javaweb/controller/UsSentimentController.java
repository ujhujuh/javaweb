package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.entity.UsSentiment;
import com.example.javaweb.service.UsSentimentService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/toolbox/us-sentiment")
public class UsSentimentController {

    @Autowired
    private UsSentimentService usSentimentService;

    @ApiLog("分页查询美股情绪指标")
    @RequiresPermissions("toolbox:us-sentiment:list")
    @GetMapping("/list")
    public Result<IPage<UsSentiment>> list(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) String startDate,
                                            @RequestParam(required = false) String endDate,
                                            @RequestParam(required = false) String satisfiedCount,
                                            @RequestParam(required = false) String notificationSent) {
        Page<UsSentiment> page = new Page<>(current, size);
        UsSentiment usSentiment = new UsSentiment();
        return Result.success(usSentimentService.selectUsSentimentList(page, usSentiment, startDate, endDate, satisfiedCount, notificationSent));
    }

    @ApiLog("查询最新美股情绪指标")
    @GetMapping("/latest")
    public Result<List<UsSentiment>> latest() {
        UsSentiment usSentiment = new UsSentiment();
        return Result.success(usSentimentService.selectUsSentimentList(usSentiment));
    }

    @ApiLog("根据ID查询美股情绪指标")
    @GetMapping("/{id}")
    public Result<UsSentiment> getById(@PathVariable Long id) {
        return Result.success(usSentimentService.selectUsSentimentById(id));
    }

    @ApiLog("手动收集美股情绪指标")
    @RequiresPermissions("toolbox:us-sentiment:collect")
    @PostMapping("/collect")
    public Result<Void> collect() {
        boolean success = usSentimentService.collectAndSaveSentimentData();
        return success ? Result.success() : Result.failed("收集数据失败");
    }

    @ApiLog("删除美股情绪指标")
    @RequiresPermissions("toolbox:us-sentiment:remove")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        usSentimentService.deleteUsSentimentByIds(Arrays.asList(ids));
        return Result.success();
    }
}