package cn.zswltech.mithras.riskcontrol.report.gljy;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.foundation.metadata.PullDownExt;

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
