package cn.zswltech.mithras.service.enums.projpricing;

import cn.zswltech.mithras.common.enums.PullDown;

public enum ProjPricingInfoModule implements PullDown {

    BASE_INFO("定价基本信息"),
    ZL_PRICE("定价租赁报价方案"),
    BL_PRICE("定价保理报价方案"),
    CASH_FACTORING_PICE("现金流量明细"),
    ZR_PRICE("定价债券转让报价方案"),
    MATERIALS_LIST("资料清单")
    ;

    ProjPricingInfoModule(String display) {
        this.display = display;
    }

    public final String display;

    public static ProjPricingInfoModule of(String code) {
        for (ProjPricingInfoModule value : ProjPricingInfoModule.values()) {
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
