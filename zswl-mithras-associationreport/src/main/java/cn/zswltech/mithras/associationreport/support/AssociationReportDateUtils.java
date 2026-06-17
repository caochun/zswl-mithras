package cn.zswltech.mithras.associationreport.support;

import cn.hutool.core.util.ObjectUtil;

import java.time.LocalDate;

public final class AssociationReportDateUtils {

    private AssociationReportDateUtils() {
    }

    public static LocalDate parseDateTime(Object o) {
        if (ObjectUtil.isEmpty(o)) {
            return null;
        }
        return cn.hutool.core.date.DateUtil.parse(o.toString()).toLocalDateTime().toLocalDate();
    }

    public static LocalDate endOfMonth(LocalDate localDate) {
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int endDay = 31;
        switch (month) {
            case 2: {
                endDay = cn.hutool.core.date.DateUtil.isLeapYear(year) ? 29 : 28;
                break;
            }
            case 4:
            case 6:
            case 9:
            case 11: {
                endDay = 30;
                break;
            }
            default: {
                break;
            }
        }
        return LocalDate.of(year, month, endDay);
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
}
