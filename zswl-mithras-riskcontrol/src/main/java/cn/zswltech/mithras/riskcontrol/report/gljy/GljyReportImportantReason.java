package cn.zswltech.mithras.riskcontrol.report.gljy;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.foundation.metadata.PullDownExt;

/**
 * @author yibin
 */
@PullDownExt("gljyReportImportantReason")
public enum GljyReportImportantReason implements PullDown {

    /**
     * 单笔交易金额达到五亿元以上
     */
    one("单笔交易金额达到五亿元以上", 21),
    two("单笔交易金额占集团金融板块上一年度末经审计的净资产的1%以上", 22),
    three("该笔交易发生后，与一个关联方的年度累计交易金额占集团金融板块上一年度末经审计的净资产5%以上", 23),
    ;

    public String display;
    public Integer code;


    GljyReportImportantReason
            (String display, Integer code) {
        this.display = display;
        this.code = code;
    }

    @Override
    public String display() {
        return display;
    }

}