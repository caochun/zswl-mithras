package cn.zswltech.mithras.fund.application.financial;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

final class FinancialSystemFormatUtil {

    private FinancialSystemFormatUtil() {
    }

    static String toRateWithoutSplit(Integer dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        return BigDecimal.valueOf(dbNumber).divide(BigDecimal.valueOf(1000000), 4, RoundingMode.HALF_UP).toPlainString();
    }

    static String toYuanWithoutSplit(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        return BigDecimal.valueOf(dbNumber).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString();
    }

    static String interestRateTypeCode(String interestRateType) {
        if ("FIXED".equals(interestRateType)) {
            return "0";
        }
        if ("FLOAT".equals(interestRateType)) {
            return "1";
        }
        return null;
    }

    static boolean isFixedRate(String rateTypeCode) {
        return "0".equals(rateTypeCode);
    }

    static String lprTypeCode(String lprType) {
        if ("ONE_YEAR".equals(lprType)) {
            return "5";
        }
        if ("FIVE_YEAR".equals(lprType)) {
            return "13";
        }
        return "1";
    }

    static String repayFrequencyCode(String repayFrequency) {
        if ("MONTH".equals(repayFrequency)) {
            return "1";
        }
        if ("DOUBLE_MONTH".equals(repayFrequency)) {
            return "2";
        }
        if ("QUARTER".equals(repayFrequency)) {
            return "3";
        }
        if ("HALF_YEAR".equals(repayFrequency)) {
            return "6";
        }
        if ("YEAR".equals(repayFrequency)) {
            return "12";
        }
        if ("NON_STAGES".equals(repayFrequency)) {
            return "0";
        }
        return null;
    }
}
