package cn.zswltech.mithras.creditreport.util;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * 征信报送计算工具类
 *
 * @author wangchuanhao
 * @date 2022/10/14 1:48 PM
 */
public class CreditReportUtil {

    /**
     * 计算月数差距，直接月数相减
     * @return
     */
    public static Integer calMonthDiff(LocalDate timeBefore, LocalDate timeAfter) {
        if (Objects.isNull(timeBefore) || Objects.isNull(timeAfter)) {
            return null;
        }
        // 这种计算方式不对
        return Math.toIntExact(Math.abs(LocalDateTimeUtil.between(timeAfter.atStartOfDay(), timeBefore.atStartOfDay(), ChronoUnit.MONTHS)));
    }

}
