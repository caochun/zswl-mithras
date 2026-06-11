package cn.zswltech.mithras.customer.enums;

import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/9/29
 * @description
 */
public enum FinanceReportCashFlowItemCode {
    I9199,
    I9200,
    I9201,
    I9202,
    I9203,
    I9204,
    I9205,
    I9206,
    I9207,
    I9208,
    I9209,
    I9210,
    I9211,
    I9212,
    I9213,
    I9214,
    I9215,
    I9216,
    I9217,
    I9218,
    I9219,
    I9220,
    I9221,
    I9222,
    I0003,
    I9223,
    I9224,
    I9225,
    I9226,
    I9227,
    I9228,
    I9229,
    I9230,
    I9231,
    I9232,
    I9233,
    I9234,
    I9235,
    I9236,
    I9237,
    I9238,
    I9239,
    I9240,
    I9241,
    I9242,
    I9243,
    I9244,
    I9245,
    I9246,
    I9247,
    I9248,
    I9249,
    I9250,
    I9251,
    I9252,
    I9253,
    I9254,
    I9255,
    I9256,
    I9257,
    I9258,
    I9259,
    I9260,
    I9261;

    public static int getOrderNumByName(String name) {
        try {
            FinanceReportCashFlowItemCode item = valueOf(name);
            return Optional.of(item).map(Enum::ordinal).orElse(Integer.MAX_VALUE);
        } catch (Exception e) {
            // do nothing
            return Integer.MAX_VALUE;
        }
    }
}
