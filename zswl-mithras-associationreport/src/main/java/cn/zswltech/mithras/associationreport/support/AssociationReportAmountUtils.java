package cn.zswltech.mithras.associationreport.support;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AssociationReportAmountUtils {

    private AssociationReportAmountUtils() {
    }

    public static BigDecimal millimeterLong2YuanBigDecimal(Long amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP);
    }
}
