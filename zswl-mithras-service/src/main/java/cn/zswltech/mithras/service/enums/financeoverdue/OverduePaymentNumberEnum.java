package cn.zswltech.mithras.service.enums.financeoverdue;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;

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
        if (ObjectUtil.isEmpty(code) || CashFlowItemEnum.EARNEST_MONEY.name().equals(code) || CashFlowItemEnum.RETENTION_MONEY.name().equals(code)) {
            return KX09;
        }
        return KX08;
    }
}
