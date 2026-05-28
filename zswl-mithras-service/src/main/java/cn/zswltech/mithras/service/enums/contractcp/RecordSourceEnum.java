package cn.zswltech.mithras.service.enums.contractcp;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @create: 2022-08-26
 **/
public enum RecordSourceEnum implements PullDown {
    MARGIN("保证金"),
    COLLECTION("收款"),
    COLLECTION_RENT("租金收款"),
    PAYMENT("付款");
    public String display;
    RecordSourceEnum(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
