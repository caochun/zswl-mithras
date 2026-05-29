package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.common.enums.PullDown;

public enum ProjReviewInfoModule implements PullDown {

    BASE_INFO("评审基本信息"),
    ZL_PRICE("评审租赁报价方案"),
    BL_PRICE("评审保理报价方案"),
    CASH_FACTORING_PICE("现金流量明细"),
    MEETING_MINUTES_CASH_FACTORING_PICE("会议纪要现金流量明细"),
    ZR_PRICE("评审债券转让报价方案"),
    MATERIALS_LIST("资料清单")
    ;

    ProjReviewInfoModule(String display) {
        this.display = display;
    }

    public final String display;

    public static ProjReviewInfoModule of(String code) {
        for (ProjReviewInfoModule value : ProjReviewInfoModule.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
