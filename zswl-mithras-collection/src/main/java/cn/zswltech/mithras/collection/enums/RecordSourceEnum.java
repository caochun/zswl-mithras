package cn.zswltech.mithras.collection.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

public enum RecordSourceEnum implements PullDown {
    MARGIN("保证金"),
    COLLECTION("收款"),
    COLLECTION_RENT("租金收款"),
    PAYMENT("付款");

    public String display;

    RecordSourceEnum(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
