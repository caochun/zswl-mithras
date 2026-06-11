package cn.zswltech.mithras.riskcontrol.report.gljy;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.foundation.metadata.PullDownExt;

/**
 * @author yibin
 */
@PullDownExt("gljyReportCategoryOne")
public enum GljyReportCategoryOne implements PullDown {

    TRZL("投融资类");

    public String display;


    GljyReportCategoryOne(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }

}