package cn.zswltech.mithras.metric.enums.risk.index;

/**
 * @author yibin
 */
public enum RiskMetricDataSource {
    MANUAL("手动添加"),
    AUTO("自动计算");

    RiskMetricDataSource(String display) {
        this.display = display;
    }

    public final String display;
}
