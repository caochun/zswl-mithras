package cn.zswltech.mithras.finance.enums.financeoverdue;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;

public enum OverduePaymentNumberEnum implements PullDown {
    KX08("租赁款"), KX09("保证金");

    OverduePaymentNumberEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static OverduePaymentNumberEnum of(String code) {
        for (OverduePaymentNumberEnum value : OverduePaymentNumberEnum.values()) {
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

    public static OverduePaymentNumberEnum switchCq(String code) {
        if (code == null || code.isEmpty() || CashFlowItemEnum.EARNEST_MONEY.name().equals(code) || CashFlowItemEnum.RETENTION_MONEY.name().equals(code)) {
            return KX09;
        }
        return KX08;
    }
}
