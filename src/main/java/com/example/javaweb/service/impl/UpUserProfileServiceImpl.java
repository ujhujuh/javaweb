package com.example.javaweb.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.*;
import com.example.javaweb.mapper.UpUserProfileMapper;
import com.example.javaweb.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UpUserProfileServiceImpl extends ServiceImpl<UpUserProfileMapper, UpUserProfile> implements UpUserProfileService {

    @Autowired
    private UpUserProfileMapper upUserProfileMapper;

    @Autowired
    private UpSoftwareUsageService upSoftwareUsageService;

    @Autowired
    private UpFileOperationService upFileOperationService;

    @Autowired
    private UpBrowserBehaviorService upBrowserBehaviorService;

    @Autowired
    private SysUserService sysUserService;

    @Override
    public IPage<UpUserProfile> selectList(IPage<UpUserProfile> page, UpUserProfile profile) {
        LambdaQueryWrapper<UpUserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(profile.getUserId() != null, UpUserProfile::getUserId, profile.getUserId())
                .orderByDesc(UpUserProfile::getProfileDate);
        return upUserProfileMapper.selectPage(page, wrapper);
    }

    @Override
    public UpUserProfile selectByUserIdAndDate(Long userId, LocalDate date) {
        return upUserProfileMapper.selectByUserIdAndDate(userId, date);
    }

    @Override
    public List<UpUserProfile> selectHistoryByUserId(Long userId) {
        return upUserProfileMapper.selectHistoryByUserId(userId);
    }

    @Override
    public List<UpUserProfile> selectByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return upUserProfileMapper.selectByDateRange(userId, startDate, endDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean generateUserProfile(Long userId, LocalDate date) {
        // 获取当日数据
        List<UpSoftwareUsage> softwareUsages = upSoftwareUsageService.selectByUserIdAndDate(userId, date);
        List<UpFileOperation> fileOperations = upFileOperationService.selectByUserIdAndDate(userId, date);
        List<UpBrowserBehavior> browserBehaviors = upBrowserBehaviorService.selectByUserIdAndDate(userId, date);

        // 创建画像对象
        UpUserProfile profile = new UpUserProfile();
        profile.setUserId(userId);
        profile.setProfileDate(date);

        // 统计数据
        int softwareStartCount = softwareUsages.size();
        int fileOperationCount = fileOperations.size();
        int browserVisitCount = browserBehaviors.size();
        int totalUsageDuration = softwareUsages.stream().mapToInt(UpSoftwareUsage::getDuration).sum();
        int onlineDuration = browserBehaviors.stream().mapToInt(UpBrowserBehavior::getDuration).sum();

        profile.setSoftwareStartCount(softwareStartCount);
        profile.setFileOperationCount(fileOperationCount);
        profile.setBrowserVisitCount(browserVisitCount);
        profile.setTotalUsageDuration(totalUsageDuration);
        profile.setOnlineDuration(onlineDuration);

        // 计算基础属性画像
        profile.setActivityLevel(calculateActivityLevel(softwareStartCount, fileOperationCount, onlineDuration));
        profile.setUsagePeriod(calculateUsagePeriod(softwareUsages, fileOperations, browserBehaviors));
        profile.setDeviceDependency(calculateDeviceDependency(totalUsageDuration, softwareUsages.size(), fileOperationCount));
        profile.setWorkRhythm(calculateWorkRhythm(softwareUsages, fileOperations, browserBehaviors, date));

        // 计算软件使用画像
        profile.setSoftwarePreference(calculateSoftwarePreference(softwareUsages));
        profile.setSoftwareDiversity(calculateSoftwareDiversity(softwareUsages));
        profile.setProfessionalLevel(calculateProfessionalLevel(softwareUsages));

        // 计算网络行为画像
        profile.setBrowserPreference(calculateBrowserPreference(browserBehaviors));
        profile.setVisitCategory(calculateVisitCategory(browserBehaviors));

        // 检查是否已存在画像，存在则更新，不存在则新增
        UpUserProfile existingProfile = upUserProfileMapper.selectByUserIdAndDate(userId, date);
        if (existingProfile != null) {
            profile.setId(existingProfile.getId());
            return upUserProfileMapper.updateById(profile) > 0;
        } else {
            return upUserProfileMapper.insert(profile) > 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchGenerateUserProfiles(LocalDate date) {
        List<SysUser> users = sysUserService.list();
        for (SysUser user : users) {
            try {
                generateUserProfile(user.getId(), date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 计算活跃度
     */
    private String calculateActivityLevel(int softwareStartCount, int fileOperationCount, int onlineDuration) {
        int totalScore = softwareStartCount + fileOperationCount + (onlineDuration / 60); // 在线时长按分钟计算
        if (totalScore >= 100) {
            return "high";
        } else if (totalScore >= 50) {
            return "medium";
        } else {
            return "low";
        }
    }

    /**
     * 计算使用时段
     */
    private String calculateUsagePeriod(List<UpSoftwareUsage> softwareUsages,
                                        List<UpFileOperation> fileOperations,
                                        List<UpBrowserBehavior> browserBehaviors) {
        Map<String, Integer> periodCount = new HashMap<>();
        periodCount.put("morning", 0);
        periodCount.put("afternoon", 0);
        periodCount.put("evening", 0);

        softwareUsages.forEach(usage -> countPeriod(usage.getStartTime(), periodCount));
        fileOperations.forEach(op -> countPeriod(op.getOperationTime(), periodCount));
        browserBehaviors.forEach(behavior -> countPeriod(behavior.getVisitTime(), periodCount));

        return periodCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
    }

    private void countPeriod(LocalDateTime time, Map<String, Integer> periodCount) {
        int hour = time.getHour();
        if (hour >= 6 && hour < 12) {
            periodCount.put("morning", periodCount.get("morning") + 1);
        } else if (hour >= 12 && hour < 18) {
            periodCount.put("afternoon", periodCount.get("afternoon") + 1);
        } else {
            periodCount.put("evening", periodCount.get("evening") + 1);
        }
    }

    /**
     * 计算设备依赖度
     */
    private String calculateDeviceDependency(int totalUsageDuration, int softwareCount, int fileOperationCount) {
        int hours = totalUsageDuration / 3600;
        if (hours >= 8 && softwareCount >= 5) {
            return "high";
        } else if (hours >= 4 && softwareCount >= 3) {
            return "medium";
        } else {
            return "low";
        }
    }

    /**
     * 计算工作节奏
     */
    private String calculateWorkRhythm(List<UpSoftwareUsage> softwareUsages,
                                        List<UpFileOperation> fileOperations,
                                        List<UpBrowserBehavior> browserBehaviors,
                                        LocalDate date) {
        Map<String, Integer> hourlyActivity = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            hourlyActivity.put(String.valueOf(i), 0);
        }

        softwareUsages.forEach(usage -> {
            int hour = usage.getStartTime().getHour();
            hourlyActivity.put(String.valueOf(hour), hourlyActivity.get(String.valueOf(hour)) + 1);
        });

        fileOperations.forEach(op -> {
            int hour = op.getOperationTime().getHour();
            hourlyActivity.put(String.valueOf(hour), hourlyActivity.get(String.valueOf(hour)) + 1);
        });

        browserBehaviors.forEach(behavior -> {
            int hour = behavior.getVisitTime().getHour();
            hourlyActivity.put(String.valueOf(hour), hourlyActivity.get(String.valueOf(hour)) + 1);
        });

        // 找出最活跃的时段
        String peakHour = hourlyActivity.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("0");

        int hour = Integer.parseInt(peakHour);
        if (hour >= 9 && hour <= 11) {
            return "上午活跃型";
        } else if (hour >= 14 && hour <= 17) {
            return "下午活跃型";
        } else if (hour >= 19 && hour <= 23) {
            return "晚上活跃型";
        } else {
            return "分散型";
        }
    }

    /**
     * 计算软件偏好
     */
    private String calculateSoftwarePreference(List<UpSoftwareUsage> softwareUsages) {
        if (softwareUsages.isEmpty()) {
            return "[]";
        }

        Map<String, Integer> softwareCount = softwareUsages.stream()
                .collect(Collectors.groupingBy(UpSoftwareUsage::getSoftwareName, Collectors.summingInt(u -> 1)));

        Map<String, Integer> softwareDuration = softwareUsages.stream()
                .collect(Collectors.groupingBy(UpSoftwareUsage::getSoftwareName, Collectors.summingInt(UpSoftwareUsage::getDuration)));

        // 按使用时长排序，取前5
        List<Map<String, Object>> topSoftware = softwareDuration.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("duration", entry.getValue());
                    map.put("count", softwareCount.get(entry.getKey()));
                    return map;
                })
                .collect(Collectors.toList());

        return JSONUtil.toJsonStr(topSoftware);
    }

    /**
     * 计算软件多样性
     */
    private int calculateSoftwareDiversity(List<UpSoftwareUsage> softwareUsages) {
        return (int) softwareUsages.stream()
                .map(UpSoftwareUsage::getSoftwareName)
                .distinct()
                .count();
    }

    /**
     * 计算专业程度
     */
    private String calculateProfessionalLevel(List<UpSoftwareUsage> softwareUsages) {
        // 专业软件列表
        Set<String> professionalSoftware = new HashSet<>(Arrays.asList(
                "Visual Studio", "IntelliJ IDEA", "Eclipse", "PyCharm", "Android Studio",
                "Photoshop", "Illustrator", "After Effects", "Premiere", "Sketch",
                "AutoCAD", "SolidWorks", "Revit", "MATLAB", "SPSS",
                "Docker", "VMware", "VirtualBox", "Postman", "Git"
        ));

        long professionalCount = softwareUsages.stream()
                .filter(usage -> professionalSoftware.contains(usage.getSoftwareName()))
                .count();

        long totalDuration = softwareUsages.stream()
                .filter(usage -> professionalSoftware.contains(usage.getSoftwareName()))
                .mapToLong(UpSoftwareUsage::getDuration)
                .sum();

        if (professionalCount >= 3 || totalDuration >= 7200) { // 2小时
            return "high";
        } else if (professionalCount >= 1 || totalDuration >= 1800) { // 30分钟
            return "medium";
        } else {
            return "low";
        }
    }

    /**
     * 计算浏览器偏好
     */
    private String calculateBrowserPreference(List<UpBrowserBehavior> browserBehaviors) {
        if (browserBehaviors.isEmpty()) {
            return "unknown";
        }

        Map<String, Long> browserCount = browserBehaviors.stream()
                .filter(behavior -> StrUtil.isNotBlank(behavior.getBrowserType()))
                .collect(Collectors.groupingBy(UpBrowserBehavior::getBrowserType, Collectors.counting()));

        return browserCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
    }

    /**
     * 计算访问类别
     */
    private String calculateVisitCategory(List<UpBrowserBehavior> browserBehaviors) {
        if (browserBehaviors.isEmpty()) {
            return "[]";
        }

        Map<String, Integer> categoryCount = new HashMap<>();
        categoryCount.put("新闻资讯", 0);
        categoryCount.put("社交媒体", 0);
        categoryCount.put("视频娱乐", 0);
        categoryCount.put("购物电商", 0);
        categoryCount.put("技术开发", 0);
        categoryCount.put("学习教育", 0);
        categoryCount.put("工作办公", 0);
        categoryCount.put("其他", 0);

        for (UpBrowserBehavior behavior : browserBehaviors) {
            String url = behavior.getUrl().toLowerCase();
            if (url.contains("news") || url.contains("163.com") || url.contains("sina.com") || url.contains("sohu.com")) {
                categoryCount.put("新闻资讯", categoryCount.get("新闻资讯") + 1);
            } else if (url.contains("weibo") || url.contains("twitter") || url.contains("facebook") || url.contains("linkedin")) {
                categoryCount.put("社交媒体", categoryCount.get("社交媒体") + 1);
            } else if (url.contains("youtube") || url.contains("bilibili") || url.contains("youku") || url.contains("iqiyi")) {
                categoryCount.put("视频娱乐", categoryCount.get("视频娱乐") + 1);
            } else if (url.contains("taobao") || url.contains("jd.com") || url.contains("amazon") || url.contains("pinduoduo")) {
                categoryCount.put("购物电商", categoryCount.get("购物电商") + 1);
            } else if (url.contains("github") || url.contains("stackoverflow") || url.contains("csdn") || url.contains("juejin")) {
                categoryCount.put("技术开发", categoryCount.get("技术开发") + 1);
            } else if (url.contains("mooc") || url.contains("coursera") || url.contains("edu") || url.contains("study")) {
                categoryCount.put("学习教育", categoryCount.get("学习教育") + 1);
            } else if (url.contains("docs") || url.contains("office") || url.contains("work") || url.contains("mail")) {
                categoryCount.put("工作办公", categoryCount.get("工作办公") + 1);
            } else {
                categoryCount.put("其他", categoryCount.get("其他") + 1);
            }
        }

        // 按访问次数排序，取前5
        List<Map<String, Object>> topCategories = categoryCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("category", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());

        return JSONUtil.toJsonStr(topCategories);
    }
}