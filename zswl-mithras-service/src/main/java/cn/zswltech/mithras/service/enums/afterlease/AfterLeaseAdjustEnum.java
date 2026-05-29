package cn.zswltech.mithras.service.enums.afterlease;

import cn.zswltech.mithras.common.enums.PullDown;

public enum AfterLeaseAdjustEnum implements PullDown {
    EXTEND("项目展期"),
    REPAYMENT("调整还款计划");

    AfterLeaseAdjustEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static AfterLeaseAdjustEnum of(String code) {
        for (AfterLeaseAdjustEnum value : AfterLeaseAdjustEnum.values()) {
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
