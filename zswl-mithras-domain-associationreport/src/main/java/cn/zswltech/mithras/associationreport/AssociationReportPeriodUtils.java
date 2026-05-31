package cn.zswltech.mithras.associationreport;

import cn.zswltech.mithras.service.enums.associationreport.AssociationReportPeriodCategoryEnum;
import cn.zswltech.mithras.service.others.MithrasException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Shared period formatting rules for association report records.
 */
public final class AssociationReportPeriodUtils {

    private AssociationReportPeriodUtils() {
    }

    public static String generatePeriod(String periodCategory, int period, int year) {
        if (Objects.equals(AssociationReportPeriodCategoryEnum.REALTIME.name(), periodCategory)) {
            return new SimpleDateFormat("yyyyMMdd").format(new Date());
        }
        if (Objects.equals(AssociationReportPeriodCategoryEnum.MONTH.name(), periodCategory)) {
            return year + String.format("%02d", period);
        }
        if (Objects.equals(AssociationReportPeriodCategoryEnum.QUARTER.name(), periodCategory)) {
            return year + "Q" + period;
        }
        throw new MithrasException("未定义的周期类型");
    }
}
