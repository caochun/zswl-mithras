package cn.zswltech.mithras.customer.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @author luyi
 */
public enum GenderType implements PullDown {
    MALE("男"), FEMALE("女");


    public String display;

    GenderType(String display) {
        this.display = display;
    }

    public static GenderType of(String name) {
        for (GenderType value : GenderType.values()) {
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
