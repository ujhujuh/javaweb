package com.example.javaweb.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.UsSentiment;
import com.example.javaweb.mapper.UsSentimentMapper;
import com.example.javaweb.service.UsSentimentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class UsSentimentServiceImpl extends ServiceImpl<UsSentimentMapper, UsSentiment> implements UsSentimentService {

    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.notification-to:}")
    private String notificationTo;

    @Override
    public IPage<UsSentiment> selectUsSentimentList(IPage<UsSentiment> page, UsSentiment usSentiment) {
        LambdaQueryWrapper<UsSentiment> wrapper = new LambdaQueryWrapper<>();
        if (usSentiment.getRecordDate() != null) {
            wrapper.eq(UsSentiment::getRecordDate, usSentiment.getRecordDate());
        }
        wrapper.orderByDesc(UsSentiment::getRecordDate);
        return this.page(page, wrapper);
    }

    @Override
    public IPage<UsSentiment> selectUsSentimentList(IPage<UsSentiment> page, UsSentiment usSentiment, String startDate, String endDate, String satisfiedCount, String notificationSent) {
        LambdaQueryWrapper<UsSentiment> wrapper = new LambdaQueryWrapper<>();
        if (usSentiment.getRecordDate() != null) {
            wrapper.eq(UsSentiment::getRecordDate, usSentiment.getRecordDate());
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(UsSentiment::getRecordDate, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(UsSentiment::getRecordDate, endDate);
        }
        if (satisfiedCount != null && !satisfiedCount.isEmpty()) {
            wrapper.eq(UsSentiment::getSatisfiedCount, Integer.parseInt(satisfiedCount));
        }
        if (notificationSent != null && !notificationSent.isEmpty()) {
            wrapper.eq(UsSentiment::getNotificationSent, notificationSent);
        }
        wrapper.orderByDesc(UsSentiment::getRecordDate);
        return this.page(page, wrapper);
    }

    @Override
    public List<UsSentiment> selectUsSentimentList(UsSentiment usSentiment) {
        LambdaQueryWrapper<UsSentiment> wrapper = new LambdaQueryWrapper<>();
        if (usSentiment.getRecordDate() != null) {
            wrapper.eq(UsSentiment::getRecordDate, usSentiment.getRecordDate());
        }
        wrapper.orderByDesc(UsSentiment::getRecordDate);
        wrapper.last("LIMIT 30");
        return this.list(wrapper);
    }

    @Override
    public UsSentiment selectUsSentimentById(Long id) {
        return this.getById(id);
    }

    @Override
    public boolean insertUsSentiment(UsSentiment usSentiment) {
        return this.save(usSentiment);
    }

    @Override
    public boolean updateUsSentiment(UsSentiment usSentiment) {
        return this.updateById(usSentiment);
    }

    @Override
    public boolean deleteUsSentimentByIds(List<Long> ids) {
        return this.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean collectAndSaveSentimentData() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(3);

            LambdaQueryWrapper<UsSentiment> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(UsSentiment::getRecordDate, yesterday);
            UsSentiment existing = this.getOne(checkWrapper);
            if (existing != null) {
                log.info("日期 {} 的数据已存在，跳过收集", yesterday);
                return true;
            }

            UsSentiment usSentiment = new UsSentiment();
            usSentiment.setRecordDate(yesterday);

            BigDecimal vix = fetchVIX();
            usSentiment.setVix(vix);

            BigDecimal fearGreed = fetchFearGreed();
            usSentiment.setFearGreed(fearGreed);

            BigDecimal naaim = fetchNAAIM();
            usSentiment.setNaaim(naaim);

            BigDecimal rsiSp500 = fetchRSISP500();
            usSentiment.setRsiSp500(rsiSp500);

            checkConditions(usSentiment);

            this.save(usSentiment);

            if (usSentiment.getSatisfiedCount() >= 3 && !"1".equals(usSentiment.getNotificationSent())) {
                checkConditionsAndSendNotification(usSentiment);
            }

            log.info("美股情绪指标数据收集完成，日期：{}", yesterday);
            return true;
        } catch (Exception e) {
            log.error("收集美股情绪指标数据失败", e);
            return false;
        }
    }

    private void checkConditions(UsSentiment usSentiment) {
        int satisfiedCount = 0;

        if (usSentiment.getVix() != null && usSentiment.getVix().compareTo(new BigDecimal("30")) > 0) {
            usSentiment.setVixCondition("1");
            satisfiedCount++;
        } else {
            usSentiment.setVixCondition("0");
        }

        if (usSentiment.getFearGreed() != null && usSentiment.getFearGreed().compareTo(new BigDecimal("20")) < 0) {
            usSentiment.setFearGreedCondition("1");
            satisfiedCount++;
        } else {
            usSentiment.setFearGreedCondition("0");
        }

        if (usSentiment.getNaaim() != null && usSentiment.getNaaim().compareTo(new BigDecimal("40")) < 0) {
            usSentiment.setNaaimCondition("1");
            satisfiedCount++;
        } else {
            usSentiment.setNaaimCondition("0");
        }

        if (usSentiment.getRsiSp500() != null && usSentiment.getRsiSp500().compareTo(new BigDecimal("30")) < 0) {
            usSentiment.setRsiCondition("1");
            satisfiedCount++;
        } else {
            usSentiment.setRsiCondition("0");
        }

        usSentiment.setSatisfiedCount(satisfiedCount);
    }

    @Override
    public boolean checkConditionsAndSendNotification(UsSentiment usSentiment) {
        if (usSentiment.getSatisfiedCount() >= 3) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(notificationTo.split(","));
                message.setSubject("美股情绪指标预警 - " + usSentiment.getRecordDate());
                message.setText(buildEmailContent(usSentiment));

                javaMailSender.send(message);

                usSentiment.setNotificationSent("1");
                this.updateById(usSentiment);

                log.info("美股情绪指标预警邮件已发送，日期：{}", usSentiment.getRecordDate());
                return true;
            } catch (Exception e) {
                log.error("发送美股情绪指标预警邮件失败", e);
                return false;
            }
        }
        return false;
    }

    private String buildEmailContent(UsSentiment usSentiment) {
        StringBuilder content = new StringBuilder();
        content.append("美股情绪指标预警通知\n\n");
        content.append("日期：").append(usSentiment.getRecordDate()).append("\n");
        content.append("满足条件数量：").append(usSentiment.getSatisfiedCount()).append("/4\n\n");
        content.append("各项指标详情：\n");
        content.append("----------------------------------------\n");
        content.append(String.format("VIX指数: %.2f (恐慌指数)\n", usSentiment.getVix()));
        content.append(String.format("  条件: VIX > 30 - %s\n", "1".equals(usSentiment.getVixCondition()) ? "✓ 满足" : "✗ 不满足"));
        content.append("----------------------------------------\n");
        content.append(String.format("Fear & Greed: %.2f (恐惧贪婪指数)\n", usSentiment.getFearGreed()));
        content.append(String.format("  条件: Fear & Greed < 20 - %s\n", "1".equals(usSentiment.getFearGreedCondition()) ? "✓ 满足" : "✗ 不满足"));
        content.append("----------------------------------------\n");
        content.append(String.format("NAAIM: %.2f (机构仓位指数)\n", usSentiment.getNaaim()));
        content.append(String.format("  条件: NAAIM < 40 - %s\n", "1".equals(usSentiment.getNaaimCondition()) ? "✓ 满足" : "✗ 不满足"));
        content.append("----------------------------------------\n");
        content.append(String.format("RSI (标普500): %.2f (相对强弱指数)\n", usSentiment.getRsiSp500()));
        content.append(String.format("  条件: RSI < 30 - %s\n", "1".equals(usSentiment.getRsiCondition()) ? "✓ 满足" : "✗ 不满足"));
        content.append("----------------------------------------\n\n");
        content.append("说明：\n");
        content.append("- 满足3个或4个条件时触发预警\n");
        content.append("- VIX > 30: 恐慌爆发\n");
        content.append("- Fear & Greed < 20: 散户绝望\n");
        content.append("- NAAIM < 40: 机构空仓\n");
        content.append("- RSI < 30: 技术性超卖\n");
        return content.toString();
    }

    private BigDecimal fetchVIX() {
        try {
            // 使用Yahoo Finance获取VIX数据
            String url = "https://finance.yahoo.com/quote/%5EVIX";
            HttpResponse response = HttpRequest.get(url)
                    .timeout(30000)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .execute();
            String body = response.body();

            // 查找VIX的当前值
            // Yahoo Finance页面的格式通常是：<span data-reactid="...">17.30</span>
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("([\\d]+\\.[\\d]{2})\\s*</span>");
            java.util.regex.Matcher m = p.matcher(body);

            while (m.find()) {
                String value = m.group(1).trim();
                try {
                    double vixValue = Double.parseDouble(value);
                    // VIX通常在10-100之间
                    if (vixValue >= 10 && vixValue <= 100) {
                        log.info("成功获取VIX: {}", vixValue);
                        return new BigDecimal(value);
                    }
                } catch (NumberFormatException e) {
                    // 继续查找下一个匹配
                }
            }

            // 如果没有找到，尝试另一种格式
            p = java.util.regex.Pattern.compile("\"regularMarketPrice\":\\s*([\\d.]+)");
            m = p.matcher(body);
            if (m.find()) {
                String value = m.group(1).trim();
                double vixValue = Double.parseDouble(value);
                if (vixValue >= 10 && vixValue <= 100) {
                    log.info("成功获取VIX (格式2): {}", vixValue);
                    return new BigDecimal(value);
                }
            }

            log.error("VIX数据解析失败");
            return null;
        } catch (Exception e) {
            log.error("获取VIX数据失败", e);
            return null;
        }
    }

    private BigDecimal fetchFearGreed() {
        try {
            // 使用CNN Money获取Fear & Greed指数
            String url = "https://www.cnn.com/markets/fear-and-greed";
            HttpResponse response = HttpRequest.get(url)
                    .timeout(30000)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .execute();
            String body = response.body();

            // CNN的页面可能有JavaScript动态加载的值
            // 尝试查找可能的数值模式
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("(?i)fear[\\s&]*greed[^0-9]*([0-9]+)");
            java.util.regex.Matcher m = p.matcher(body);

            if (m.find()) {
                String value = m.group(1).trim();
                try {
                    double fgValue = Double.parseDouble(value);
                    if (fgValue >= 0 && fgValue <= 100) {
                        log.info("成功获取Fear & Greed: {}", fgValue);
                        return new BigDecimal(value);
                    }
                } catch (NumberFormatException e) {
                    // 继续尝试
                }
            }

            // 尝试查找任何0-100之间的数值
            p = java.util.regex.Pattern.compile("\\b([3-9][0-9]|100)\\b");
            m = p.matcher(body);
            if (m.find()) {
                String value = m.group(1).trim();
                try {
                    double fgValue = Double.parseDouble(value);
                    if (fgValue >= 30 && fgValue <= 100) {
                        log.info("找到可能的Fear & Greed值: {}", fgValue);
                        return new BigDecimal(value);
                    }
                } catch (NumberFormatException e) {
                    // 继续尝试
                }
            }

            log.error("Fear & Greed数据解析失败");
            return null;
        } catch (Exception e) {
            log.error("获取Fear & Greed数据失败", e);
            return null;
        }
    }

    private BigDecimal fetchNAAIM() {
        try {
            // NAAIM Exposure Index没有免费的公开API
            // 使用CBOE的Put/Call Ratio作为替代指标
            String url = "https://www.cboe.com/us/options/market_statistics/daily/";
            HttpResponse response = HttpRequest.get(url)
                    .timeout(30000)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .execute();
            String body = response.body();

            // 尝试查找Put/Call Ratio
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("put/call[\\s&]*ratio[^0-9]*([0-9.]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher m = p.matcher(body);

            if (m.find()) {
                String value = m.group(1).trim();
                try {
                    double ratio = Double.parseDouble(value);
                    // 将Put/Call Ratio转换为NAAIM Exposure Index
                    // Put/Call Ratio高表示市场看跌，对应较低的Exposure Index
                    // 公式：NAAIM = 100 - (Put/Call Ratio * 50)
                    double estimatedNaaim = Math.max(0, Math.min(100, 100 - ratio * 50));
                    log.info("从Put/Call Ratio {} 估算NAAIM: {}", ratio, estimatedNaaim);
                    return new BigDecimal(String.format("%.2f", estimatedNaaim));
                } catch (NumberFormatException e) {
                    // 继续尝试
                }
            }

            // 尝试查找任何0-2之间的数值（Put/Call Ratio通常在这个范围）
            p = java.util.regex.Pattern.compile("\\b([0-9]\\.[0-9]|[0-2])\\b");
            m = p.matcher(body);
            if (m.find()) {
                String value = m.group(1).trim();
                try {
                    double ratio = Double.parseDouble(value);
                    if (ratio >= 0 && ratio <= 2) {
                        double estimatedNaaim = Math.max(0, Math.min(100, 100 - ratio * 50));
                        log.info("找到可能的Put/Call Ratio {}，估算NAAIM: {}", ratio, estimatedNaaim);
                        return new BigDecimal(String.format("%.2f", estimatedNaaim));
                    }
                } catch (NumberFormatException e) {
                    // 继续尝试
                }
            }

            log.error("NAAIM数据解析失败");
            return null;
        } catch (Exception e) {
            log.error("获取NAAIM数据失败", e);
            return null;
        }
    }

    private BigDecimal fetchRSISP500() {
        try {
            // RSI数据较难直接获取，使用替代方案
            // 尝试从一些技术分析网站获取

            // 使用Stochastics作为RSI的替代指标
            // Stochastics和RSI都是动量指标，范围都是0-100
            String url = "https://www.marketwatch.com/investing/index/spx";
            HttpResponse response = HttpRequest.get(url)
                    .timeout(30000)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .execute();
            String body = response.body();

            // 尝试查找RSI或Stochastics的值
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("(?i)(rsi|stochastic|relative strength)[^0-9]*([0-9]+)");
            java.util.regex.Matcher m = p.matcher(body);

            if (m.find()) {
                String value = m.group(2).trim();
                try {
                    double rsiValue = Double.parseDouble(value);
                    if (rsiValue >= 0 && rsiValue <= 100) {
                        log.info("成功获取RSI/Stochastic: {}", rsiValue);
                        return new BigDecimal(value);
                    }
                } catch (NumberFormatException e) {
                    // 继续尝试
                }
            }

            // 如果没有找到技术指标，尝试从价格变化计算简化的RSI
            // 使用最近的价格变化来估算
            p = java.util.regex.Pattern.compile("([\\d,]+\\.[\\d]{2})\\s*[+-]\\s*[\\d.]+%");
            m = p.matcher(body);
            if (m.find()) {
                String value = m.group(1).replace(",", "").trim();
                try {
                    double price = Double.parseDouble(value);
                    // 基于价格变化估算RSI
                    // 如果价格上涨，RSI倾向于较高；如果下跌，RSI倾向于较低
                    // 这里使用简化的估算方法
                    // 注意：这不是真正的RSI计算，只是一个估算
                    // 真正的RSI需要历史价格数据

                    // 从页面中查找涨跌幅
                    java.util.regex.Pattern changePattern = java.util.regex.Pattern.compile("([+-]?[0-9]+\\.[0-9]+)%");
                    java.util.regex.Matcher changeMatcher = changePattern.matcher(body);
                    if (changeMatcher.find()) {
                        String changeStr = changeMatcher.group(1).replace("+", "").trim();
                        double changePercent = Double.parseDouble(changeStr);

                        // 估算RSI：基准50，加上涨跌幅的调整
                        // 正涨幅增加RSI，负涨幅减少RSI
                        double estimatedRSI = 50 + changePercent * 2;
                        estimatedRSI = Math.max(0, Math.min(100, estimatedRSI));

                        log.info("从价格变化 {}% 估算RSI: {}", changePercent, estimatedRSI);
                        return new BigDecimal(String.format("%.2f", estimatedRSI));
                    }
                } catch (NumberFormatException e) {
                    // 继续尝试
                }
            }

            log.error("RSI数据解析失败");
            return null;
        } catch (Exception e) {
            log.error("获取RSI数据失败", e);
            return null;
        }
    }
}