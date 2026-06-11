package cn.zswltech.mithras.finance.enums.financeoverdue;

import cn.zswltech.mithras.foundation.metadata.PullDown;

public enum OverdueRecordTypeEnum implements PullDown {
    SETTLEMENT("结算"), INTEGRATION("集成"),
    ;

    OverdueRecordTypeEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static OverdueRecordTypeEnum of(String code) {
        for (OverdueRecordTypeEnum value : OverdueRecordTypeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
