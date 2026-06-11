package cn.zswltech.mithras.policy.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

public enum PolicyDataStatusEnum implements PullDown {
    FORMAL("正式"),
    TEMP("暂存")
    ;
    PolicyDataStatusEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static PolicyDataStatusEnum of(String code) {
        for (PolicyDataStatusEnum value : PolicyDataStatusEnum.values()) {
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
