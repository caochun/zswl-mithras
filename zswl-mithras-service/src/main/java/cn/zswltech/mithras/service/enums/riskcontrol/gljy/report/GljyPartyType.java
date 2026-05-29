package cn.zswltech.mithras.service.enums.riskcontrol.gljy.report;

import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;

/**
 * @author yibin
 */
@PullDownExt("gljyPartyType")
public enum GljyPartyType implements PullDown {
    CORPORATION("法人", 1),
    NORMAL("自然人", 2);


    public String display;
    public int code;

    GljyPartyType(String display, int code) {
        this.display = display;
        this.code = code;
    }

    @Override
    public String display() {
        return display;
    }
}
