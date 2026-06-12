package cn.zswltech.mithras.basedata.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataSpecialDate;
import cn.zswltech.mithras.basedata.service.BaseDataSpecialDateService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//
public class WorkdayWeekUtil {

    // 结果封装类
    public static class WorkweekResult {
        private final LocalDate firstWorkday;
        private final LocalDate lastWorkday;

        public WorkweekResult(LocalDate firstWorkday, LocalDate lastWorkday) {
            this.firstWorkday = firstWorkday;
            this.lastWorkday = lastWorkday;
        }

        public LocalDate getFirstWorkday() { return firstWorkday; }
        public LocalDate getLastWorkday() { return lastWorkday; }
    }

    // 查询某一天的下一周工作日区间
    public static WorkweekResult calculateWorkweek(LocalDate targetDate) {

        // 1. 找到目标周的范围（周一到周日）
        LocalDate weekStart = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        BaseDataSpecialDateService baseDataSpecialDateService = SpringUtil.getBean(BaseDataSpecialDateService.class);
        List<BaseDataSpecialDate> specialDates = baseDataSpecialDateService.findBySpecialDateAfter(weekStart.minusDays(1));
        Map<LocalDate, BaseDataSpecialDate> specialDateMap = specialDates.stream().collect(Collectors.toMap(BaseDataSpecialDate::getSpecialDate
                , e -> e));

        // 2. 检查目标周是否有工作日
        WorkweekResult currentWeek = findWorkdaysInWeek(weekStart, specialDateMap);
        boolean shouldCheckNextWeek = currentWeek == null || 
            (currentWeek != null && targetDate.equals(currentWeek.getFirstWorkday()));

        // 3. 确定起始查找周
        LocalDate searchStart = shouldCheckNextWeek ? weekStart.plusWeeks(1) : weekStart;

        // 4. 向后查找有效周
        while (true) {
            WorkweekResult result = findWorkdaysInWeek(searchStart, specialDateMap);
            if (result != null) return result;
            searchStart = searchStart.plusWeeks(1);
        }
    }

    // 查找指定周的工作日 monday 指定周周一
    private static WorkweekResult findWorkdaysInWeek(LocalDate monday, Map<LocalDate, BaseDataSpecialDate> specialDateMap) {
        LocalDate first = null;
        LocalDate last = null;

        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            if (DateUtil.isWorkday(specialDateMap, date)) {
                if (first == null) first = date;
                last = date;
            }
        }
        return first != null ? new WorkweekResult(first, last) : null;
    }

    /**
     * @return 如果目标日期是当周第一个有效工作日返回 true
     */
    public static boolean isDateFirstWorkday(LocalDate targetDate) {
        // 获取本周周一
        LocalDate weekStart = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        BaseDataSpecialDateService baseDataSpecialDateService = SpringUtil.getBean(BaseDataSpecialDateService.class);
        List<BaseDataSpecialDate> specialDates = baseDataSpecialDateService.findBySpecialDateAfter(weekStart.minusDays(1));
        Map<LocalDate, BaseDataSpecialDate> specialDateMap = specialDates.stream().collect(Collectors.toMap(BaseDataSpecialDate::getSpecialDate
                , e -> e));

        // 遍历本周已过日期（周一到今日）
        for (LocalDate date = weekStart; !date.isAfter(targetDate); date = date.plusDays(1)) {
            if (DateUtil.isWorkday(specialDateMap, date)) {
                // 发现第一个工作日时，判断是否为目标日
                return date.equals(targetDate);
            }
        }
        return false;
    }

    /*
    public static void main(String[] args) {
        // 测试用例：假设目标日是当周第一个工作日
        LocalDate testDate = LocalDate.of(2023, 10, 9); // 周一
        WorkweekResult result = calculateWorkweek(testDate);
        System.out.println("Next first workday: " + result.getFirstWorkday());
        System.out.println("Next last workday: " + result.getLastWorkday());
    }*/
}