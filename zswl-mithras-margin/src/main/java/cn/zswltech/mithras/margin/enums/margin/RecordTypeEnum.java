package cn.zswltech.mithras.margin.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @create: 2022-08-18
 **/

public enum RecordTypeEnum  implements PullDown {
    /**
     * 收款记录-recordType 记录类型
     */
    COLLECTION("收款"),
    /**
     * 收款记录-collectionType 收款类型
     */
    WIRE_TRANSFER("电汇"),


    /**
     * 退款记录 recordType 记录类型
     */
    REFUND("退款"),
    REFUND_WARRANTY("质保金退款"),
    /**
     * 退款记录-collectionType 付款方式
     */
    REFUND_MARGIN("保证金退款"),
    REFUND_MARGIN_DEDUCT("保证金抵扣");




    RecordTypeEnum(String display) {
        this.display = display;
    }

    public final String display;


    public static RecordTypeEnum of(String name) {
        for (RecordTypeEnum value : RecordTypeEnum.values()) {
            if (value.name().equals(name)) {
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
