package cn.zswltech.mithras.service.enums.riskcontrol.gljy.report;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;

/**
 * @author yibin
 */
@PullDownExt("gljyReportStatus")
public enum GljyReportStatus implements PullDown {

    NOT_REPORT("未报送"), REPORTED("已报送");


    public String display;

    GljyReportStatus(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
