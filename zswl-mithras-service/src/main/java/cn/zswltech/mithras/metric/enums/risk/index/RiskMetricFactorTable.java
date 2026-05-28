package cn.zswltech.mithras.metric.enums.risk.index;

/**
 * @author yibin
 */
public enum RiskMetricFactorTable {

    BANK_INTEREST_REPAY("银行还息统计表"),
    //
    CAPITAL_BALANCE("资产负债表"),
    PROFIT("利润表"),
    CASH_FLOW("现金流量表"),
    SUBJECT_BALANCE("科目余额表"),
    GZKB("国资快报");

    public final String display;

    RiskMetricFactorTable(String display) {
        this.display = display;
    }

    public static RiskMetricFactorTable ofDisplay(String display) {
        for (RiskMetricFactorTable value : RiskMetricFactorTable.values()) {
            if (value.display.trim().equals(display)) {
                return value;
            }
        }
        return null;
    }

    public static RiskMetricFactorTable ofName(String name) {
        for (RiskMetricFactorTable value : RiskMetricFactorTable.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
