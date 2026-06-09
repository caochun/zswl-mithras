package cn.zswltech.mithras.dashboard.application.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class DashboardAmountUtil {

    private DashboardAmountUtil() {
    }

    public static String toYuanWithoutSplit(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        return BigDecimal.valueOf(dbNumber).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String toWanYuanWithoutSplit(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        return BigDecimal.valueOf(dbNumber).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String toYiYuanWithoutSplit(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        return BigDecimal.valueOf(dbNumber).divide(new BigDecimal("1000000000000"), 2, RoundingMode.HALF_UP).toPlainString();
    }
}
