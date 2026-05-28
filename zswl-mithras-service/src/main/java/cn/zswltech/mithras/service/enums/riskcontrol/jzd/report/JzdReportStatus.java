package cn.zswltech.mithras.service.enums.riskcontrol.jzd.report;

/**
 * @author yibin
 */
public enum JzdReportStatus {

    NOT_REPORT("未报送"), REPORTED("已报送");


    public String display;

    JzdReportStatus(String display) {
        this.display = display;
    }
}
