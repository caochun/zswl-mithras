package cn.zswltech.mithras.metric.enums.risk.index;

/**
 * @author yibin
 */
public enum RiskMetricStatus {

    REPORT_FAIL("报送失败"),

    REPORTED("已报送"),

    PEND_REPORT("待报送"),

    PEND_FILL("待填值");

    RiskMetricStatus(String display) {
        this.display = display;
    }

    public final String display;
}
