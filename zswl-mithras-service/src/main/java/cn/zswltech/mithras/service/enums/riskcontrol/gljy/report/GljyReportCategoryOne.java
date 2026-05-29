package cn.zswltech.mithras.service.enums.riskcontrol.gljy.report;

import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;

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