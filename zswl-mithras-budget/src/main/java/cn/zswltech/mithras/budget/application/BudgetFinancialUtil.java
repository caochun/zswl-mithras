package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.service.enums.projestablish.LeaseType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class BudgetFinancialUtil {

    public static BigDecimal ensureConsultingTaxRate() {
        return BigDecimal.valueOf(0.06);
    }

    public static BigDecimal ensureValueAddedTaxRate(String leaseType) {
        if (Objects.equals(leaseType, LeaseType.hui_zu.name())) {
            return BigDecimal.valueOf(0.06);
        }
        if (Objects.equals(leaseType, LeaseType.zhi_zu.name())) {
            return BigDecimal.valueOf(0.13);
        }
        if (Objects.equals(leaseType, LeaseType.jyx_zu.name())) {
            return BigDecimal.valueOf(0.13);
        }
        return null;
    }

    public static Long mithrasLongDecimalTwo(Long v) {
        if (v == null) {
            return null;
        }
        return new BigDecimal(v).divide(new BigDecimal(10000L))
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(10000L))
                .longValue();
    }

    public static Integer mithrasIntegerDecimalTwo(Integer v) {
        if (v == null) {
            return null;
        }
        return new BigDecimal(v).divide(new BigDecimal(10000L))
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(10000L))
                .intValue();
    }
}
