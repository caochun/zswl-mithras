package cn.zswltech.mithras.metric.enums.risk.index;

/**
 * @author yibin
 */
public enum RiskMetricType {
    ZLX("指令性"),
    FZLX("非指令性");

    RiskMetricType(String display) {
        this.display = display;
    }

    public final String display;
}
