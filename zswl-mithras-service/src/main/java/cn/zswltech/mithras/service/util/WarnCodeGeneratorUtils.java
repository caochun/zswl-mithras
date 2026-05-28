package cn.zswltech.mithras.service.util;

import cn.zswltech.mithras.service.mapper.riskcontrol.RiskControlWarnMonitorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class WarnCodeGeneratorUtils {

    private static final String PREFIX = "YJ";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int NUMBER_LENGTH = 4; // 编号位数
    private static final int MAX_NUMBER = 9999; // 4位数字最大支持9999

    // 用于存储每个日期对应的计数器
    private final ConcurrentHashMap<String, AtomicInteger> dateCounterMap = new ConcurrentHashMap<>();
    // 保证线程安全
    private final ReentrantLock lock = new ReentrantLock();

    @Resource
    private RiskControlWarnMonitorMapper riskControlWarnMonitorMapper;

    /**
     * 生成单个warn_code
     */
    public String generateWarnCode() {
        return generateWarnCode(LocalDate.now());
    }

    /**
     * 生成单个warn_code（指定日期）
     */
    public String generateWarnCode(LocalDate date) {
        String dateStr = date.format(DATE_FORMATTER);
        AtomicInteger counter = dateCounterMap.get(dateStr);

        if (counter == null) {
            initializeCounterForDate(dateStr);
            counter = dateCounterMap.get(dateStr);
        }

        int number = counter.getAndIncrement();
        return formatWarnCode(dateStr, number);
    }

    /**
     * 批量生成warn_code
     * @param count 生成数量
     * @return warn_code列表
     */
    public List<String> generateBatchWarnCodes(int count) {
        return generateBatchWarnCodes(LocalDate.now(), count);
    }

    /**
     * 批量生成warn_code（指定日期）
     * @param date 日期
     * @param count 生成数量
     * @return warn_code列表
     */
    public List<String> generateBatchWarnCodes(LocalDate date, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("生成数量必须大于0");
        }

        String dateStr = date.format(DATE_FORMATTER);
        return doGenerateWarnCodes(dateStr, count);
    }

    /**
     * 实际的生成逻辑
     */
    private List<String> doGenerateWarnCodes(String dateStr, int count) {
        if (count <= 0 || count > MAX_NUMBER) {
            throw new IllegalArgumentException(
                    String.format("生成数量必须在1-%d之间", MAX_NUMBER)
            );
        }

        lock.lock();
        try {
            // 获取或初始化计数器
            AtomicInteger counter = dateCounterMap.get(dateStr);
            if (counter == null) {
                initializeCounterForDate(dateStr);
                counter = dateCounterMap.get(dateStr);
            }

            // 检查剩余编号数量
            int currentValue = counter.get();
            if (currentValue + count - 1 > MAX_NUMBER) {
                throw new IllegalStateException(
                        String.format("日期 %s 的预警编号已超过最大限制(%d)，当前值: %d，需要: %d",
                                dateStr, MAX_NUMBER, currentValue, count)
                );
            }

            // 生成编号列表
            List<String> warnCodes = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                int number = counter.getAndIncrement();
                String warnCode = String.format("%s%s%04d", PREFIX, dateStr, number);
                warnCodes.add(warnCode);
            }

            log.info("生成 {} 个预警编号，日期: {}，起始编号: {}",
                    count, dateStr, currentValue);
            return warnCodes;

        } finally {
            lock.unlock();
        }
    }

    /**
     * 初始化计数器，从数据库中加载当天的最大编号
     */
    @PostConstruct
    public void initCounter() {
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DATE_FORMATTER);
        initializeCounterForDate(todayStr);
    }

    /**
     * 为指定日期初始化计数器
     */
    private void initializeCounterForDate(String dateStr) {
        String maxWarnCode = null;
        try {
            // 查询数据库中当天最大的warn_code
            maxWarnCode = riskControlWarnMonitorMapper.selectMaxWarnCodeByDate(dateStr);
        } catch (Exception e) {
            // 数据库查询异常，使用默认值
            log.warn("查询最大warn_code异常，使用默认起始值", e);
        }

        int startNumber = calculateStartNumber(maxWarnCode, dateStr);
        dateCounterMap.put(dateStr, new AtomicInteger(startNumber));
    }
    /**
     * 计算起始编号
     * 处理maxWarnCode为null/空/格式不正确的情况
     */
    private int calculateStartNumber(String maxWarnCode, String dateStr) {
        // 情况1：maxWarnCode为null或空字符串
        if (!StringUtils.hasText(maxWarnCode)) {
            return 1; // 当天没有数据，从1开始
        }

        // 情况2：检查长度是否符合要求
        if (maxWarnCode.length() < PREFIX.length() + dateStr.length() + NUMBER_LENGTH) {
            log.warn("maxWarnCode长度不足，使用默认起始值: {}", maxWarnCode);
            return 1;
        }

        // 情况3：检查前缀和日期部分
        String expectedPrefix = PREFIX + dateStr;
        if (!maxWarnCode.startsWith(expectedPrefix)) {
            log.warn("maxWarnCode格式不正确，使用默认起始值: {}", maxWarnCode);
            return 1;
        }

        // 情况4：提取编号部分
        try {
            String numberPart = maxWarnCode.substring(expectedPrefix.length());
            // 检查编号部分是否全是数字
            if (!numberPart.matches("\\d{" + NUMBER_LENGTH + "}")) {
                log.warn("maxWarnCode编号部分格式不正确，使用默认起始值: {}", maxWarnCode);
                return 1;
            }

            int maxNumber = Integer.parseInt(numberPart);
            if (maxNumber < 0 || maxNumber >= Math.pow(10, NUMBER_LENGTH) - 1) {
                log.warn("maxWarnCode编号超出范围，使用默认起始值: {}", maxWarnCode);
                return 1;
            }

            return maxNumber + 1;
        } catch (NumberFormatException e) {
            log.warn("maxWarnCode编号部分解析失败，使用默认起始值: {}", maxWarnCode);
            return 1;
        }
    }

    /**
     * 格式化warn_code
     */
    private String formatWarnCode(String dateStr, int number) {
        // 检查编号是否超出范围
        int maxNumber = (int) Math.pow(10, NUMBER_LENGTH) - 1;
        if (number > maxNumber) {
            throw new IllegalStateException(
                    String.format("当日预警编号已超过最大限制(%d)，无法生成新编号", maxNumber)
            );
        }

        return String.format("%s%s%0" + NUMBER_LENGTH + "d", PREFIX, dateStr, number);
    }
}
