package cn.zswltech.mithras.basedata.util;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.basedata.enums.BaseDataSpecialDateTypeEnum;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataSpecialDate;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.basedata.service.BaseDataSpecialDateService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 日期
 *
 * @author wangchuanhao
 * @date 2022/6/21 12:46 PM
 */
public class DateUtil {
    public static final String DATE_TIME_PATTERN = "yyyyMMdd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyyMMdd";

    public static LocalDate ensureQuarterFirstDay(int year, int quarter) {
        int targetMonth;
        switch (quarter) {
            case 1: {
                targetMonth = 1;
                break;
            }
            case 2: {
                targetMonth = 4;
                break;
            }
            case 3: {
                targetMonth = 7;
                break;
            }
            case 4: {
                targetMonth = 10;
                break;
            }
            default: {
                throw new RuntimeException("非法的季度参数");
            }
        }
        return LocalDate.of(year, targetMonth, 1);
    }

    public static LocalDate ensureQuarterLastDay(int year, int quarter) {
        int targetMonth;
        int targetDay;
        switch (quarter) {
            case 1: {
                targetMonth = 3;
                targetDay = 31;
                break;
            }
            case 2: {
                targetMonth = 6;
                targetDay = 30;
                break;
            }
            case 3: {
                targetMonth = 9;
                targetDay = 30;
                break;
            }
            case 4: {
                targetMonth = 12;
                targetDay = 31;
                break;
            }
            default: {
                throw new RuntimeException("非法的季度参数");
            }
        }
        return LocalDate.of(year, targetMonth, targetDay);
    }

    public static int[] ensureLastYearQuarter(LocalDateTime localDateTime) {
        int currentMonth = localDateTime.getMonthValue();
        int lastMonth = currentMonth == 1 ? 12 : currentMonth - 1;
        int lastQuarter = ensureQuarter(lastMonth);
        int currentYear = localDateTime.getYear();
        int lastQuarterYear = currentMonth == 1 ? currentYear - 1 : currentYear;
        return new int[]{lastQuarterYear, lastQuarter};
    }

    public static int ensureQuarter(int month) {
        switch (month) {
            case 1:
            case 2:
            case 3:
                return 1;
            case 4:
            case 5:
            case 6:
                return 2;
            case 7:
            case 8:
            case 9:
                return 3;
            case 10:
            case 11:
            case 12:
                return 4;
            default: {
                throw new MithrasException("非法的月份");
            }
        }
    }

    public static LocalDateTime timestamp2LDT(long timestamp) {
        return LocalDateTimeUtil.of(timestamp);
//        return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    public static boolean isWorkday(LocalDate localDate) {
        BaseDataSpecialDateService baseDataSpecialDateService = SpringUtil.getBean(BaseDataSpecialDateService.class);
        BaseDataSpecialDate specialDate = baseDataSpecialDateService.find(localDate);
        if (Objects.nonNull(specialDate)) {
            return Objects.equals(specialDate.getSpecialType(), BaseDataSpecialDateTypeEnum.WORKDAY.name());
        }
        return !LocalDateTimeUtil.isWeekend(localDate);
    }

    /**
     * @param beganDate  开始时间
     * @param targetDate 目标时间
     *                   返回目标时间是开始时间后的第几个工作日
     * @author: jackerhe
     * @date: 2023/1/8 4:01 下午
     **/
    public static Integer countWorkdayNumber(LocalDate beganDate, LocalDate targetDate) {
        Integer days = 0;
        BaseDataSpecialDateService baseDataSpecialDateService = SpringUtil.getBean(BaseDataSpecialDateService.class);
        List<BaseDataSpecialDate> specialDates = baseDataSpecialDateService.findBySpecialDateAfter(beganDate);
        Map<LocalDate, BaseDataSpecialDate> specialDateMap = specialDates.stream().collect(Collectors.toMap(BaseDataSpecialDate::getSpecialDate
                , e -> e));
        while (!beganDate.isAfter(targetDate)) {
            if (isWorkday(specialDateMap, beganDate)) {
                days++;
            }
            beganDate = beganDate.plusDays(1);
        }
        return days;
    }

    /**
     * @param beganDate  开始时间
     * @param targetDate 目标时间
     *                   返回目标时间是开始时间后的第几个工作日
     **/
    public static Long safeCountWorkdayNumber(LocalDateTime beganDate, LocalDateTime targetDate) {
        if (Objects.isNull(beganDate) || Objects.isNull(targetDate)){
            return 0L;
        }
        return Long.valueOf(countWorkdayNumber(beganDate.toLocalDate(), targetDate.toLocalDate()));
    }


    /**
     * @param beganDateTime  开始时间
     * @param targetDateTime 目标时间
     *                   返回目标时间是开始时间后的第几个工作日小时
     * @author: jackerhe
     * @date: 2023/1/8 4:01 下午
     **/
    public static Long countWorkdayHouse(LocalDateTime beganDateTime, LocalDateTime targetDateTime) {
        if(ObjectUtil.isEmpty(beganDateTime) || ObjectUtil.isEmpty(targetDateTime)){
            return null;
        }
        long minutes = ChronoUnit.MINUTES.between(beganDateTime, targetDateTime);//总时数
        int beganHouse = beganDateTime.getMonthValue();
        int beganSecond = beganDateTime.getSecond();
        int endHouse = targetDateTime.getMonthValue();
        int endSecond = targetDateTime.getSecond();

        LocalDate beganDate = beganDateTime.toLocalDate();
        LocalDate endDate = targetDateTime.toLocalDate();
        LocalDate tempDate = beganDate;
        BaseDataSpecialDateService baseDataSpecialDateService = SpringUtil.getBean(BaseDataSpecialDateService.class);
        //查询所有 -- 存在ThreadLocal中，一个线程只会查询一次数据库
        List<BaseDataSpecialDate> specialDates = baseDataSpecialDateService.findAllSpecialDate();
        Map<LocalDate, BaseDataSpecialDate> specialDateMap = specialDates.stream().collect(Collectors.toMap(BaseDataSpecialDate::getSpecialDate
                , e -> e));
        while (!tempDate.isAfter(endDate)) {
            if (!isWorkday(specialDateMap, tempDate)) {
                //非工作日减小时
                if (tempDate.equals(beganDate)) {
                    minutes = minutes - ((beganHouse -1) * 60L + beganSecond);
                } else if (tempDate.equals(endDate)) {
                    minutes = minutes - ((endHouse -1) * 60L + endSecond);
                } else {
                    minutes = minutes - 24*60L;
                }
            }
            tempDate = tempDate.plusDays(1);
        }
        return minutes;
    }

    /**
     * 判断specialDates特殊时间下，targetDate是否为工作日
     *
     * @author: jackerhe
     * @date: 2023/1/10 5:26 下午
     **/
    public static boolean isWorkday(Map<LocalDate, BaseDataSpecialDate> specialDateMap, LocalDate targetDate) {
        BaseDataSpecialDate specialDate = specialDateMap.get(targetDate);
        if (Objects.nonNull(specialDate)) {
            return Objects.equals(specialDate.getSpecialType(), BaseDataSpecialDateTypeEnum.WORKDAY.name());
        }
        return !LocalDateTimeUtil.isWeekend(targetDate);
    }


    public static String getMonthStr(LocalDate date) {
        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
    }

    /**
     * 获取某季度的开始日期
     *
     * @param offset 0本季度，1下个季度，-1上个季度，依次类推
     * @return
     */
    public static LocalDateTime quarterStart(int offset) {
        final LocalDate date = LocalDate.now().plusMonths(offset * 3L);
        int month = date.getMonth().getValue();
        int start;
        if (month <= 3) {
            start = 1;
        } else if (month <= 6) {
            start = 4;
        } else if (month <= 9) {
            start = 7;
        } else {
            start = 10;
        }
        LocalDate firstDate = date.plusMonths(start - month).with(TemporalAdjusters.firstDayOfMonth());
        return firstDate.atStartOfDay();
    }

    public static boolean isQuarterStart(LocalDate date) {
        int month = date.getMonth().getValue();
        return month == 1 || month == 4 || month == 7 || month == 10;
    }

    public static LocalDateTime startOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay();
    }

    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atTime(23, 59, 59);
    }

    public static LocalDate startOfMonth(LocalDate localDate) {
        return LocalDate.of(localDate.getYear(), localDate.getMonthValue(), 1);
    }

    public static LocalDate endOfMonth(LocalDate localDate) {
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int endDay = 31;
        switch (month) {
            case 2: {
                if (cn.hutool.core.date.DateUtil.isLeapYear(year)) {
                    endDay = 29;
                } else {
                    endDay = 28;
                }
                break;
            }
            case 4:
            case 6:
            case 9:
            case 11: {
                endDay = 30;
                break;
            }
        }
        return LocalDate.of(year, month, endDay);
    }

    public static String dateString(LocalDate date, String pattern) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate parseDateTime(Object o) {
        if (ObjectUtil.isEmpty(o)) {
            return null;
        }
        return cn.hutool.core.date.DateUtil.parse(o.toString()).toLocalDateTime().toLocalDate();
    }

    /**
     * 计算指定日期之后或之前指定工作日天数的下一个工作日日期
     *
     * @param startDate 起始日期，如果为null则使用当前日期
     * @param workdays  工作日天数，正数表示向后查找，负数表示向前查找
     * @return 下一个工作日日期
     */
    public static LocalDate safeGetNextWorkdayAfterDays(LocalDateTime startDate, int workdays) {
        if (Objects.isNull(startDate)){
            return getNextWorkdayAfterDays(LocalDate.now(), workdays);
        }
        return getNextWorkdayAfterDays(startDate.toLocalDate(), workdays);
    }

    /**
     * 计算指定日期之后或之前指定工作日天数的下一个工作日日期
     *
     * @param startDate 起始日期，如果为null则使用当前日期
     * @param workdays  工作日天数，正数表示向后查找，负数表示向前查找
     * @return 下一个工作日日期
     */
    public static LocalDate getNextWorkdayAfterDays(LocalDate startDate, int workdays) {
        if (workdays == 0) {
            throw new IllegalArgumentException("工作日天数不能为0");
        }

        LocalDate currentDate = startDate != null ? startDate : LocalDate.now();
        BaseDataSpecialDateService baseDataSpecialDateService = SpringUtil.getBean(BaseDataSpecialDateService.class);
        List<BaseDataSpecialDate> specialDates = baseDataSpecialDateService.findAllSpecialDate();
        Map<LocalDate, BaseDataSpecialDate> specialDateMap = specialDates.stream()
                .collect(Collectors.toMap(BaseDataSpecialDate::getSpecialDate, e -> e));

        LocalDate targetDate = currentDate;

        if (workdays > 0) {
            // 向后查找
            int countedWorkdays = 0;

            // 如果起始日期是工作日，需要排除当天
            if (isWorkday(specialDateMap, targetDate)) {
                targetDate = targetDate.plusDays(1);
            }

            // 计算指定工作日天数后的日期
            while (countedWorkdays < workdays) {
                if (isWorkday(specialDateMap, targetDate)) {
                    countedWorkdays++;
                }
                if (countedWorkdays < workdays) {
                    targetDate = targetDate.plusDays(1);
                }
            }

            // 再往后找到下一个工作日
            do {
                targetDate = targetDate.plusDays(1);
            } while (!isWorkday(specialDateMap, targetDate));

        } else {
            // 向前查找 (workdays < 0)
            int countedWorkdays = 0;
            int targetWorkdays = Math.abs(workdays);

            // 如果起始日期是工作日，需要排除当天
            if (isWorkday(specialDateMap, targetDate)) {
                targetDate = targetDate.minusDays(1);
            }

            // 计算指定工作日天数前的日期
            while (countedWorkdays < targetWorkdays) {
                if (isWorkday(specialDateMap, targetDate)) {
                    countedWorkdays++;
                }
                if (countedWorkdays < targetWorkdays) {
                    targetDate = targetDate.minusDays(1);
                }
            }

            // 再往前找到下一个工作日
            do {
                targetDate = targetDate.minusDays(1);
            } while (!isWorkday(specialDateMap, targetDate));
        }

        return targetDate;
    }

    public static Long between(LocalDate beginDate, LocalDate endDate,DateUnit dateUnit) {
        Date beginParse = cn.hutool.core.date.DateUtil.parse(beginDate.toString());
        Date endDateParse = cn.hutool.core.date.DateUtil.parse(endDate.toString());
        return cn.hutool.core.date.DateUtil.between(beginParse, endDateParse, dateUnit);
    }

}
