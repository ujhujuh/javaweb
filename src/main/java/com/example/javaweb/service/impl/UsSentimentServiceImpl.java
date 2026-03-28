package com.example.javaweb.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.dto.UsSentimentCollectResultDTO;
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
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UsSentimentServiceImpl extends ServiceImpl<UsSentimentMapper, UsSentiment> implements UsSentimentService {

    private static final String SOURCE_FRED_VIX = "FRED_VIX";
    private static final String SOURCE_STOOQ_VIX = "STOOQ_VIX";
    private static final String SOURCE_YAHOO_VIX = "YAHOO_VIX";
    private static final String SOURCE_CNN_FEAR_GREED = "CNN_FEAR_GREED";
    private static final String SOURCE_CBOE_NAAIM = "CBOE_NAAIM";
    private static final String SOURCE_FRED_SP500 = "FRED_SP500";

    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.notification-to:}")
    private String notificationTo;

    @Value("${us-sentiment.fetch.timeout-ms:15000}")
    private int fetchTimeoutMs;

    @Value("${us-sentiment.fetch.retry-times:2}")
    private int fetchRetryTimes;

    @Value("${us-sentiment.fetch.retry-interval-ms:1000}")
    private long fetchRetryIntervalMs;

    @Value("${us-sentiment.fetch.user-agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36}")
    private String fetchUserAgent;

    @Value("${us-sentiment.enabled-sources:FRED_VIX,STOOQ_VIX,YAHOO_VIX,CNN_FEAR_GREED,CBOE_NAAIM,FRED_SP500}")
    private String enabledSources;

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
    public UsSentimentCollectResultDTO collectAndSaveSentimentData() {
        UsSentimentCollectResultDTO result = new UsSentimentCollectResultDTO();
        Map<String, String> failedIndicators = new LinkedHashMap<>();
        result.setFailedIndicators(failedIndicators);

        try {
            LocalDate recordDate = resolveLatestTradingDate(LocalDate.now());

            LambdaQueryWrapper<UsSentiment> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(UsSentiment::getRecordDate, recordDate);
            UsSentiment existing = this.getOne(checkWrapper);
            if (existing != null) {
                result.setSuccess(true);
                result.setSaved(false);
                result.setSuccessCount(countSuccessfulMetrics(existing));
                result.setMessage("日期 " + recordDate + " 的数据已存在，跳过收集");
                return result;
            }

            Set<String> sourceSet = getEnabledSourceSet();
            UsSentiment usSentiment = new UsSentiment();
            usSentiment.setRecordDate(recordDate);

            FetchValue vixFetch = fetchVix(sourceSet);
            usSentiment.setVix(vixFetch.value);
            if (vixFetch.value == null) {
                failedIndicators.put("VIX", vixFetch.error);
            }

            FetchValue fearGreedFetch = fetchFearGreed(sourceSet);
            usSentiment.setFearGreed(fearGreedFetch.value);
            if (fearGreedFetch.value == null) {
                failedIndicators.put("FearGreed", fearGreedFetch.error);
            }

            FetchValue naaimFetch = fetchNaaim(sourceSet);
            usSentiment.setNaaim(naaimFetch.value);
            if (naaimFetch.value == null) {
                failedIndicators.put("NAAIM", naaimFetch.error);
            }

            FetchValue rsiFetch = fetchRsiSp500(sourceSet);
            usSentiment.setRsiSp500(rsiFetch.value);
            if (rsiFetch.value == null) {
                failedIndicators.put("RSI_SP500", rsiFetch.error);
            }

            int successCount = 4 - failedIndicators.size();
            result.setSuccessCount(successCount);

            if (successCount == 0) {
                result.setSuccess(false);
                result.setSaved(false);
                result.setMessage("4项指标全部采集失败，未入库");
                log.error("美股情绪采集失败: recordDate={}, failedIndicators={}", recordDate, failedIndicators);
                return result;
            }

            checkConditions(usSentiment);
            this.save(usSentiment);

            if (usSentiment.getSatisfiedCount() >= 3 && !"1".equals(usSentiment.getNotificationSent())) {
                checkConditionsAndSendNotification(usSentiment);
            }

            result.setSuccess(true);
            result.setSaved(true);
            result.setMessage(String.format("4项中成功%d项，已入库", successCount));
            log.info("美股情绪采集完成: recordDate={}, successCount={}, failedCount={}, failedIndicators={}",
                    recordDate, successCount, failedIndicators.size(), failedIndicators);
            return result;
        } catch (Exception e) {
            log.error("收集美股情绪指标数据失败", e);
            result.setSuccess(false);
            result.setSaved(false);
            result.setMessage("采集执行异常: " + e.getMessage());
            return result;
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
        content.append(String.format("VIX指数: %s (恐慌指数)\n", formatMetric(usSentiment.getVix())));
        content.append(String.format("  条件: VIX > 30 - %s\n", "1".equals(usSentiment.getVixCondition()) ? "✓ 满足" : "✗ 不满足"));
        content.append("----------------------------------------\n");
        content.append(String.format("Fear & Greed: %s (恐惧贪婪指数)\n", formatMetric(usSentiment.getFearGreed())));
        content.append(String.format("  条件: Fear & Greed < 20 - %s\n", "1".equals(usSentiment.getFearGreedCondition()) ? "✓ 满足" : "✗ 不满足"));
        content.append("----------------------------------------\n");
        content.append(String.format("NAAIM: %s (机构仓位指数)\n", formatMetric(usSentiment.getNaaim())));
        content.append(String.format("  条件: NAAIM < 40 - %s\n", "1".equals(usSentiment.getNaaimCondition()) ? "✓ 满足" : "✗ 不满足"));
        content.append("----------------------------------------\n");
        content.append(String.format("RSI (标普500): %s (相对强弱指数)\n", formatMetric(usSentiment.getRsiSp500())));
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

    private String formatMetric(BigDecimal value) {
        return value == null ? "--" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private FetchValue fetchVix(Set<String> sourceSet) {
        List<String> errors = new ArrayList<>();
        FetchValue value;

        value = fetchVixFromFred(sourceSet);
        if (value.value != null) {
            return value;
        }
        errors.add(value.error);

        value = fetchVixFromStooq(sourceSet);
        if (value.value != null) {
            return value;
        }
        errors.add(value.error);

        value = fetchVixFromYahoo(sourceSet);
        if (value.value != null) {
            return value;
        }
        errors.add(value.error);

        return FetchValue.fail("VIX采集失败: " + String.join("; ", errors));
    }

    private FetchValue fetchVixFromFred(Set<String> sourceSet) {
        if (!sourceSet.contains(SOURCE_FRED_VIX)) {
            return FetchValue.fail(SOURCE_FRED_VIX + "未启用");
        }

        String body = fetchWithRetry("VIX", SOURCE_FRED_VIX, "https://fred.stlouisfed.org/graph/fredgraph.csv?id=VIXCLS");
        if (body == null) {
            return FetchValue.fail(SOURCE_FRED_VIX + "请求失败");
        }

        BigDecimal value = parseLastCsvValue(body);
        if (isInRange(value, 5, 120)) {
            return FetchValue.ok(value, SOURCE_FRED_VIX);
        }
        return FetchValue.fail(SOURCE_FRED_VIX + "解析失败");
    }

    private FetchValue fetchVixFromStooq(Set<String> sourceSet) {
        if (!sourceSet.contains(SOURCE_STOOQ_VIX)) {
            return FetchValue.fail(SOURCE_STOOQ_VIX + "未启用");
        }

        String body = fetchWithRetry("VIX", SOURCE_STOOQ_VIX, "https://stooq.com/q/l/?s=%5evix&i=d");
        if (body == null) {
            return FetchValue.fail(SOURCE_STOOQ_VIX + "请求失败");
        }

        String[] parts = body.trim().split(",");
        if (parts.length < 4) {
            return FetchValue.fail(SOURCE_STOOQ_VIX + "CSV格式异常");
        }

        BigDecimal value = parseDecimal(parts[3]);
        if (isInRange(value, 5, 120)) {
            return FetchValue.ok(value, SOURCE_STOOQ_VIX);
        }
        return FetchValue.fail(SOURCE_STOOQ_VIX + "解析失败");
    }

    private FetchValue fetchVixFromYahoo(Set<String> sourceSet) {
        if (!sourceSet.contains(SOURCE_YAHOO_VIX)) {
            return FetchValue.fail(SOURCE_YAHOO_VIX + "未启用");
        }

        String body = fetchWithRetry("VIX", SOURCE_YAHOO_VIX, "https://finance.yahoo.com/quote/%5EVIX");
        if (body == null) {
            return FetchValue.fail(SOURCE_YAHOO_VIX + "请求失败");
        }

        BigDecimal value = parseYahooVix(body);
        if (isInRange(value, 5, 120)) {
            return FetchValue.ok(value, SOURCE_YAHOO_VIX);
        }
        return FetchValue.fail(SOURCE_YAHOO_VIX + "解析失败");
    }

    private FetchValue fetchFearGreed(Set<String> sourceSet) {
        if (!sourceSet.contains(SOURCE_CNN_FEAR_GREED)) {
            return FetchValue.fail(SOURCE_CNN_FEAR_GREED + "未启用");
        }

        String body = fetchWithRetry("FearGreed", SOURCE_CNN_FEAR_GREED, "https://www.cnn.com/markets/fear-and-greed");
        if (body == null) {
            return FetchValue.fail(SOURCE_CNN_FEAR_GREED + "请求失败");
        }

        BigDecimal value = parseFearGreedFromCnn(body);
        if (isInRange(value, 0, 100)) {
            return FetchValue.ok(value, SOURCE_CNN_FEAR_GREED);
        }
        return FetchValue.fail(SOURCE_CNN_FEAR_GREED + "解析失败");
    }

    private FetchValue fetchNaaim(Set<String> sourceSet) {
        if (!sourceSet.contains(SOURCE_CBOE_NAAIM)) {
            return FetchValue.fail(SOURCE_CBOE_NAAIM + "未启用");
        }

        String body = fetchWithRetry("NAAIM", SOURCE_CBOE_NAAIM, "https://www.cboe.com/us/options/market_statistics/daily/");
        if (body == null) {
            return FetchValue.fail(SOURCE_CBOE_NAAIM + "请求失败");
        }

        BigDecimal ratio = parsePutCallRatioFromCboe(body);
        if (ratio == null || ratio.compareTo(BigDecimal.ZERO) < 0 || ratio.compareTo(new BigDecimal("5")) > 0) {
            return FetchValue.fail(SOURCE_CBOE_NAAIM + "Put/Call Ratio解析失败");
        }

        BigDecimal estimatedNaaim = new BigDecimal("100").subtract(ratio.multiply(new BigDecimal("50")));
        if (estimatedNaaim.compareTo(BigDecimal.ZERO) < 0) {
            estimatedNaaim = BigDecimal.ZERO;
        }
        if (estimatedNaaim.compareTo(new BigDecimal("100")) > 0) {
            estimatedNaaim = new BigDecimal("100");
        }
        estimatedNaaim = estimatedNaaim.setScale(2, RoundingMode.HALF_UP);

        return FetchValue.ok(estimatedNaaim, SOURCE_CBOE_NAAIM);
    }

    private FetchValue fetchRsiSp500(Set<String> sourceSet) {
        if (!sourceSet.contains(SOURCE_FRED_SP500)) {
            return FetchValue.fail(SOURCE_FRED_SP500 + "未启用");
        }

        String body = fetchWithRetry("RSI_SP500", SOURCE_FRED_SP500, "https://fred.stlouisfed.org/graph/fredgraph.csv?id=SP500");
        if (body == null) {
            return FetchValue.fail(SOURCE_FRED_SP500 + "请求失败");
        }

        List<BigDecimal> closes = parseCsvValues(body);
        BigDecimal rsi = calculateRsi(closes, 14);
        if (isInRange(rsi, 0, 100)) {
            return FetchValue.ok(rsi, SOURCE_FRED_SP500);
        }
        return FetchValue.fail(SOURCE_FRED_SP500 + "RSI计算失败(样本不足或数据异常)");
    }

    String fetchWithRetry(String indicator, String source, String url) {
        int totalAttempts = Math.max(1, fetchRetryTimes + 1);
        for (int attempt = 1; attempt <= totalAttempts; attempt++) {
            try {
                HttpResponse response = HttpRequest.get(url)
                        .timeout(fetchTimeoutMs)
                        .header("User-Agent", fetchUserAgent)
                        .execute();
                int status = response.getStatus();
                if (status == 200) {
                    String body = response.body();
                    if (body != null && !body.trim().isEmpty()) {
                        return body;
                    }
                    log.warn("{} {} 第{}次请求返回空内容", indicator, source, attempt);
                } else {
                    log.warn("{} {} 第{}次请求失败: HTTP {}", indicator, source, attempt, status);
                }
            } catch (Exception e) {
                log.warn("{} {} 第{}次请求异常: {}", indicator, source, attempt, e.getMessage());
            }

            if (attempt < totalAttempts && fetchRetryIntervalMs > 0) {
                try {
                    Thread.sleep(fetchRetryIntervalMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return null;
    }

    LocalDate resolveLatestTradingDate(LocalDate now) {
        LocalDate date = now.minusDays(1);
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return date;
    }

    BigDecimal calculateRsi(List<BigDecimal> closes, int period) {
        if (closes == null || closes.size() < period + 1) {
            return null;
        }

        int startIndex = closes.size() - (period + 1);
        double gain = 0D;
        double loss = 0D;

        for (int i = startIndex + 1; i < closes.size(); i++) {
            double diff = closes.get(i).doubleValue() - closes.get(i - 1).doubleValue();
            if (diff > 0) {
                gain += diff;
            } else if (diff < 0) {
                loss += -diff;
            }
        }

        double avgGain = gain / period;
        double avgLoss = loss / period;

        if (avgGain == 0D && avgLoss == 0D) {
            return new BigDecimal("50.00");
        }
        if (avgLoss == 0D) {
            return new BigDecimal("100.00");
        }

        double rs = avgGain / avgLoss;
        double rsi = 100D - (100D / (1D + rs));
        return new BigDecimal(rsi).setScale(2, RoundingMode.HALF_UP);
    }

    BigDecimal parseLastCsvValue(String csvBody) {
        List<BigDecimal> values = parseCsvValues(csvBody);
        if (values.isEmpty()) {
            return null;
        }
        return values.get(values.size() - 1);
    }

    List<BigDecimal> parseCsvValues(String csvBody) {
        List<BigDecimal> values = new ArrayList<>();
        if (csvBody == null || csvBody.trim().isEmpty()) {
            return values;
        }

        String[] lines = csvBody.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("DATE")) {
                continue;
            }
            String[] parts = trimmed.split(",");
            if (parts.length < 2) {
                continue;
            }
            BigDecimal value = parseDecimal(parts[1]);
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }

    BigDecimal parseYahooVix(String body) {
        Pattern p = Pattern.compile("\"regularMarketPrice\"\\s*:\\s*\\{[^}]*\"raw\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(body);
        if (m.find()) {
            return parseDecimal(m.group(1));
        }

        p = Pattern.compile("\"regularMarketPrice\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)", Pattern.CASE_INSENSITIVE);
        m = p.matcher(body);
        if (m.find()) {
            return parseDecimal(m.group(1));
        }
        return null;
    }

    BigDecimal parseFearGreedFromCnn(String body) {
        List<Pattern> patterns = Arrays.asList(
                Pattern.compile("\"fear_and_greed\"\\s*:\\s*\\{[^}]*\"score\"\\s*:\\s*(\\d{1,3})", Pattern.CASE_INSENSITIVE),
                Pattern.compile("Fear\\s*&\\s*Greed[^0-9]{0,30}(\\d{1,3})", Pattern.CASE_INSENSITIVE),
                Pattern.compile("\"score\"\\s*:\\s*(\\d{1,3})", Pattern.CASE_INSENSITIVE)
        );

        for (Pattern p : patterns) {
            Matcher m = p.matcher(body);
            if (m.find()) {
                BigDecimal value = parseDecimal(m.group(1));
                if (isInRange(value, 0, 100)) {
                    return value;
                }
            }
        }
        return null;
    }

    BigDecimal parsePutCallRatioFromCboe(String body) {
        List<Pattern> patterns = Arrays.asList(
                Pattern.compile("(?is)put\\s*/\\s*call\\s*ratio[^0-9]{0,120}([0-9]+(?:\\.[0-9]+)?)"),
                Pattern.compile("(?is)\"putCallRatio\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)")
        );

        for (Pattern p : patterns) {
            Matcher m = p.matcher(body);
            if (m.find()) {
                BigDecimal ratio = parseDecimal(m.group(1));
                if (ratio != null && ratio.compareTo(BigDecimal.ZERO) >= 0 && ratio.compareTo(new BigDecimal("5")) <= 0) {
                    return ratio;
                }
            }
        }
        return null;
    }

    private BigDecimal parseDecimal(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.trim().replace(",", "");
        if (normalized.isEmpty() || ".".equals(normalized)) {
            return null;
        }
        try {
            return new BigDecimal(normalized).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isInRange(BigDecimal value, double min, double max) {
        if (value == null) {
            return false;
        }
        BigDecimal minValue = new BigDecimal(String.valueOf(min));
        BigDecimal maxValue = new BigDecimal(String.valueOf(max));
        return value.compareTo(minValue) >= 0 && value.compareTo(maxValue) <= 0;
    }

    private Set<String> getEnabledSourceSet() {
        Set<String> sources = new LinkedHashSet<>();
        if (enabledSources == null || enabledSources.trim().isEmpty()) {
            return sources;
        }
        String[] parts = enabledSources.split(",");
        for (String part : parts) {
            if (part != null && !part.trim().isEmpty()) {
                sources.add(part.trim().toUpperCase());
            }
        }
        return sources;
    }

    private int countSuccessfulMetrics(UsSentiment usSentiment) {
        int count = 0;
        if (usSentiment.getVix() != null) {
            count++;
        }
        if (usSentiment.getFearGreed() != null) {
            count++;
        }
        if (usSentiment.getNaaim() != null) {
            count++;
        }
        if (usSentiment.getRsiSp500() != null) {
            count++;
        }
        return count;
    }

    private static class FetchValue {
        private final BigDecimal value;
        private final String error;

        private FetchValue(BigDecimal value, String error) {
            this.value = value;
            this.error = error;
        }

        private static FetchValue ok(BigDecimal value, String source) {
            return new FetchValue(value, null == source ? null : source + "成功");
        }

        private static FetchValue fail(String error) {
            return new FetchValue(null, error);
        }
    }
}
