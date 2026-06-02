package cn.zswltech.mithras.riskcontrol.report.gljy;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;

/**
 * @author yibin
 */
@PullDownExt("gljyReportLevel")
public enum GljyReportLevel implements PullDown {

    NORMAL("一般关联交易", 1),
    IMPORTANT("重大关联交易", 2);

    public String display;
    public Integer code;


    GljyReportLevel
            (String display, Integer code) {
        this.display = display;
        this.code = code;
    }

    @Override
    public String display() {
        return display;
    }

}