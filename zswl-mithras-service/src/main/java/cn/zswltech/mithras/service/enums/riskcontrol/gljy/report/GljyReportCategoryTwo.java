package cn.zswltech.mithras.service.enums.riskcontrol.gljy.report;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;

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