package cn.zswltech.mithras.collection.application;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CollectionFinancialUtil {

    public static BigDecimal mithrasLong2BigDecimal(Long value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value).divide(new BigDecimal(10000L));
    }

    public static BigDecimal mithrasInteger2BigDecimal(Integer value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value).divide(new BigDecimal(10000L));
    }

    public static Long mithrasLongDecimalTwo(Long value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value).divide(new BigDecimal(10000L))
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(10000L))
                .longValue();
    }
}
