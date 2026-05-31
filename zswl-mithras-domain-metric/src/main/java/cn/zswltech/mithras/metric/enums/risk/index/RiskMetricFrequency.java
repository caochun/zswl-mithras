package cn.zswltech.mithras.metric.enums.risk.index;

/**
 * @author yibin
 */
public enum RiskMetricFrequency {
    /*DAY("天"), WEEK("周"), */MONTH("月"), SEASON("季"), HALF_YEAR("半年"), YEAR("年");

    RiskMetricFrequency(String display) {
        this.display = display;
    }

    public final String display;

    public static RiskMetricFrequency ofDisplay(String display) {
        for (RiskMetricFrequency value : RiskMetricFrequency.values()) {
            if (value.display.trim().equals(display)) {
                return value;
            }
        }
        return null;
    }
}
