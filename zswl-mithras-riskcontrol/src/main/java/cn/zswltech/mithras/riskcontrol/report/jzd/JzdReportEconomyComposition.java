package cn.zswltech.mithras.riskcontrol.report.jzd;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.foundation.metadata.PullDownExt;

/**
 * @author yibin
 */
@PullDownExt("jzdReportEconomyComposition")
public enum JzdReportEconomyComposition implements PullDown {

    GYJJ(110, "国有经济"), JTJJ(120, "集体经济"),

    SYJJ(210, "私有经济"), GATJJ(220, "港澳台经济"),

    WSJJ(230, "外商经济"), FQYFR(900, "非企业法人");

    public String display;
    public int code;

    JzdReportEconomyComposition(int code, String display) {
        this.code = code;
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
