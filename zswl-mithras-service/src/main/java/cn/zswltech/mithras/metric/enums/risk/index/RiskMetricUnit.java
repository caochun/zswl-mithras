package cn.zswltech.mithras.metric.enums.risk.index;

/**
 * @author yibin
 */
public enum RiskMetricUnit {
    /**
     * 金额单位
     */
    YI("亿元"), WAN("万"), YUAN("元"), REN("人"), GE("个"), TIAN("天"), NIAN("年"), BI("笔"), JIAN("件"), CI("次"), PERCENT("%"), RATE("-"), HU("户");

    RiskMetricUnit(String display) {
        this.display = display;
    }

    public final String display;

    public static RiskMetricUnit ofDisplay(String display) {
        for (RiskMetricUnit value : RiskMetricUnit.values()) {
            if (value.display.trim().equals(display)) {
                return value;
            }
        }
        return null;
    }

}
