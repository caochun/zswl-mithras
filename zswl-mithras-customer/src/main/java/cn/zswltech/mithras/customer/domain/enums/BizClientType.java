package cn.zswltech.mithras.customer.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

public enum BizClientType implements PullDown {
    /*法人*/
    guarantee("担保人"),

    creditor("债权人"),

    pledgor("质押人"),

    mortgagor("抵押人");

    BizClientType(String display) {
        this.display = display;
    }

    public final String display;

    public String getDisplay() {
        return display;
    }

    @Override
    public String display() {
        return display;
    }
}
