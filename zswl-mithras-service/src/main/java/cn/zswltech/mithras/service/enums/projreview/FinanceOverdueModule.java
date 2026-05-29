package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.common.enums.PullDown;

public enum FinanceOverdueModule implements PullDown {

    BASE_INFO("逾期报送基本信息"),
    OVERDUE_INTEGRATION("应收逾期集成"),
    OVERDUE_SETTLEMENT("应收逾期集成结算"),
    ;

    FinanceOverdueModule(String display) {
        this.display = display;
    }

    public final String display;

    public static FinanceOverdueModule of(String code) {
        for (FinanceOverdueModule value : FinanceOverdueModule.values()) {
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
