package cn.zswltech.mithras.collection.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @create: 2022-08-18
 **/
public enum BillTypeEnum implements PullDown {
    COLLECTION("收款"),
    PAYMENT("付款"),
    FINANCE_COLLECTION("资金端收款"),
    FINANCE_PAYMENT("资金端付款");
    public String display;
    BillTypeEnum(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
