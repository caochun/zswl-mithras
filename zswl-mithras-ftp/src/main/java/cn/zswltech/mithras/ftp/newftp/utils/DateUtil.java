package cn.zswltech.mithras.ftp.newftp.utils;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @author yangxiong
 * @date 2024/3/31/15:39
 * @description
 */
public class DateUtil {

    /**
     * 获取上个月所在季度的第一天
     *
     * @param localDate
     * @return
     */
    @NotNull
    public static LocalDate getQuarterBegin(LocalDate date) {
        int quarterFirstMonth = ((date.getMonthValue() - 1) / 3) * 3 + 1;
        return LocalDate.of(date.getYear(), quarterFirstMonth, 1);
    }

    /**
     * 获取上个月所在季度的最后一天
     *
     * @param localDate
     * @return
     */
    @NotNull
    public static LocalDate getQuarterEnd(LocalDate date) {
        return getQuarterBegin(date).plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
    }
}
