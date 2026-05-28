package cn.zswltech.mithras.metric.enums.risk.index;

/**
 * @author yibin
 */
public enum RiskMetricFactorType {
    CALC("系统计算"),
    IMPORT("报表导入"),
    AUTOMATIC("自动获取");

    RiskMetricFactorType(String display) {
        this.display = display;
    }

    public final String display;
}
