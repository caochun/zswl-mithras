package cn.zswltech.mithras.liquidity.service;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.liquidity.snapshot.LiquiditySpecialDateSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Local calendar rules used by liquidity calculation.
 */
public final class LiquidityWorkdayCalendar {

    private static final String HOLIDAY = "HOLIDAY";

    private LiquidityWorkdayCalendar() {}

    public static List<LocalDate> handleHoliday(Map<LocalDate, LiquiditySpecialDateSnapshot> specialDateMap, LocalDate localDate) {
        List<LocalDate> resultDate = new ArrayList<>();
        if (!isFreeDay(specialDateMap.get(localDate), localDate)) {
            do {
                resultDate.add(localDate);
                localDate = localDate.plusDays(1);
            } while (isFreeDay(specialDateMap.get(localDate), localDate));
        }
        return resultDate;
    }

    private static boolean isFreeDay(LiquiditySpecialDateSnapshot specialDate, LocalDate determinedDate) {
        if (determinedDate == null) {
            throw new MithrasException("判断是否为节假日失败，日期不得为空");
        }
        if (specialDate != null) {
            return Objects.equals(specialDate.getSpecialType(), HOLIDAY);
        }
        return LocalDateTimeUtil.isWeekend(determinedDate);
    }
}
