package cn.zswltech.mithras.metric.enums.risk.index;

/**
 * @author yibin
 */
public enum RiskMetricCurrency {
    CNY("人民币"),
    USD("美元"),
    EUR("欧元"),
    JPY("日元"),
    GBP("英镑"),
    HKD("港币")
    ;

    RiskMetricCurrency(String display) {
        this.display = display;
    }

    public final String display;

    public static RiskMetricCurrency findByDisplay(String display) {
        for (RiskMetricCurrency item : values()) {
            if (item.display.equals(display)) {
                return item;
            }
        }
        return null;
    }
}
