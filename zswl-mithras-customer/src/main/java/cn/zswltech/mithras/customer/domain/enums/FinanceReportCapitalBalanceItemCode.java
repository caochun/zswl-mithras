package cn.zswltech.mithras.customer.domain.enums;

import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/9/29
 * @description 资产负债表科目编码
 */
public enum FinanceReportCapitalBalanceItemCode {
    G9100,
    G0002,
    G0003,
    G9101,
    G0004,
    G9102,
    G9103,
    G0005,
    G9104,
    G9105,
    G9106,
    G9107,
    G9108,
    G0006,
    G9109,
    G9110,
    G9111,
    G0007,
    G0008,
    G0009,
    G9112,
    G9113,
    G0010,
    G0011,
    G9114,
    G9115,
    G0012,
    G0013,
    G9116,
    G9117,
    G9120,
    G9118,
    G9119,
    G9121,
    G9122,
    G0014,
    G9123,
    G9124,
    G9125,
    G9126,
    G9127,
    G9128,
    G9129,
    G0001,
    G9130,
    G9131,
    G9132,
    G0015,
    G9133,
    G9134,
    G9135,
    G0016,
    G0017,
    G0018,
    G9137,
    G9138,
    G9136,
    G9139,
    G9140,
    G9141,
    G9142,
    G9143,
    G9144,
    G9145,
    G0019,
    G9146,
    G9147,
    G0020,
    G9148,
    G0023,
    G9149,
    G9150,
    G9151,
    G9152,
    G9153,
    G9154,
    G9155,
    G0021,
    G0022,
    G9156,
    G9157,
    G0024,
    G0025,
    G9158,
    G9159;

    public static int getOrderNumByName(String name) {
        try {
            FinanceReportCapitalBalanceItemCode item = valueOf(name);
            return Optional.of(item).map(Enum::ordinal).orElse(Integer.MAX_VALUE);
        } catch (Exception e) {
            // do nothing
            return Integer.MAX_VALUE;
        }
    }
}
