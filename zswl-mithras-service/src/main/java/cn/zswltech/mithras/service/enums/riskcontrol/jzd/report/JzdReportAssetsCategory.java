package cn.zswltech.mithras.service.enums.riskcontrol.jzd.report;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;

/**
 * @author yibin
 */
@PullDownExt("jzdReportAssetsCategory")
public enum JzdReportAssetsCategory implements PullDown {

    ZCL(1, "正常类"), GZL(2, "关注类"),

    CJL(3, "次级类"), KYL(4, "可疑类"),

    SSL(5, "损失类"), WFL(6, "未分类");

    public String display;
    public int code;

    JzdReportAssetsCategory(int code, String display) {
        this.code = code;
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
