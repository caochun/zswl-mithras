package cn.zswltech.mithras.service.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.service.enums.afterlease.RentCollectionLevelEnum;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * @create: 2022-11-19
 **/

public class CollectionLevelUtil {

    public static void main(String[] args) {
        System.out.println(getOverdueDay(LocalDateTimeUtil.parse("2022-09-01", DatePattern.NORM_DATE_PATTERN).toLocalDate()));
    }

    public static long getOverdueDay(LocalDate planCollectionDate){
        return getOverdueDay(planCollectionDate, LocalDate.now());
    }

    public static long getOverdueDay(LocalDate planCollectionDate, LocalDate targetDate){
        if (planCollectionDate == null){
            return 0;
        }
        return planCollectionDate.until(targetDate, ChronoUnit.DAYS);
    }

    public static RentCollectionLevelEnum getCollectionLevel(long day){
        if (day == 0){
            return null;
        }else if (day > 0 && day <= 30){
            return RentCollectionLevelEnum.LEVEL_30;
        }else if (day > 30 && day <= 60){
            return RentCollectionLevelEnum.LEVEL_60;
        }else if (day > 60 && day <= 90){
            return RentCollectionLevelEnum.LEVEL_90;
        }else if (day > 90){
            return RentCollectionLevelEnum.LEVEL_MAX;
        }else {
            return null;
        }
    }
}
