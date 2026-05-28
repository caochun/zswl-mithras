package cn.zswltech.mithras.service.service.newftp.utils;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Month;

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
        int quarter = cn.zswltech.mithras.service.util.DateUtil.ensureQuarter(date.getMonthValue());
        return cn.zswltech.mithras.service.util.DateUtil.ensureQuarterFirstDay(date.getYear(), quarter);
    }

    /**
     * 获取上个月所在季度的最后一天
     *
     * @param localDate
     * @return
     */
    @NotNull
    public static LocalDate getQuarterEnd(LocalDate date) {
        int quarter = cn.zswltech.mithras.service.util.DateUtil.ensureQuarter(date.getMonthValue());
        return cn.zswltech.mithras.service.util.DateUtil.ensureQuarterLastDay(date.getYear(), quarter);
    }
}
