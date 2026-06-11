package cn.zswltech.mithras.riskcontrol.report.gljy;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.foundation.metadata.PullDownExt;

/**
 * @author yibin
 */
@PullDownExt("gljyReportCategoryTwo")
public enum GljyReportCategoryTwo implements PullDown {


    RZZL("融资租赁");

    public String display;


    GljyReportCategoryTwo(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }

}