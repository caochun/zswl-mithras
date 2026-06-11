package cn.zswltech.mithras.riskcontrol.report.gljy;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.foundation.metadata.PullDownExt;

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
