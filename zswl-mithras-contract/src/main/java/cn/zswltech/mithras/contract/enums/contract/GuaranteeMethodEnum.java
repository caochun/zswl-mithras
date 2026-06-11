package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.foundation.metadata.PullDown;

public enum GuaranteeMethodEnum implements PullDown {


JOINT_RESPONSIBILITY("连带责任"), GENERAL_GUARANTEE("一般担保");

    GuaranteeMethodEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static GuaranteeMethodEnum of(String code) {
        for (GuaranteeMethodEnum value : GuaranteeMethodEnum.values()) {
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
