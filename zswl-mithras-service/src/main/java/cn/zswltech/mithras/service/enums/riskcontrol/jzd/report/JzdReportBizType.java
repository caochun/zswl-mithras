package cn.zswltech.mithras.service.enums.riskcontrol.jzd.report;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;

/**
 * @author yibin
 */
@PullDownExt("jzdReportBizType")
public enum JzdReportBizType implements PullDown {

    ZSZL_SXLYW_BL_BL("保理"), ZSZL_SXLYW_RZZL_RZZL("融资租赁");

    public String display;

    JzdReportBizType(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
