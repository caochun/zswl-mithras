package cn.zswltech.mithras.service.enums.riskcontrol.jzd.report;

/**
 * @author yibin
 */
public enum JzdReportCreateType {

    MANUALLY("手动创建"), TIMELY("定时创建");

    public String display;

    JzdReportCreateType(String display) {
        this.display = display;
    }
}
