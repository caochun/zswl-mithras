package cn.zswltech.mithras.customer.enums;

import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/9/29
 * @description 利润表科目编码
 */
public enum FinanceReportProfitItemCode {
    H9170,
    H9171,
    H9172,
    H9176,
    H9173,
    H9174,
    H0007,
    H9175,
    H0001,
    H9177,
    H9178,
    H9179,
    H0002,
    H0003,
    H0004,
    H9180,
    H9181,
    H9182,
    H9183,
    H9184,
    H9185,
    H9186,
    H0005,
    H0006,
    H9187,
    H9188;

    public static int getOrderNumByName(String name) {
        try {
            FinanceReportProfitItemCode item = valueOf(name);
            return Optional.of(item).map(Enum::ordinal).orElse(Integer.MAX_VALUE);
        } catch (Exception e) {
            // do nothing
            return Integer.MAX_VALUE;
        }
    }
}
