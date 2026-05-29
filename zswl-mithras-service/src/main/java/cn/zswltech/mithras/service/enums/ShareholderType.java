package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @author luyi
 */
public enum ShareholderType implements PullDown {
    LEGAL_PERSON("法人"), NORMAL_PERSON("自然人"), OTHER("其他");

    ShareholderType(String display) {
        this.display = display;
    }

    public final String display;

    public static ShareholderType of(String code) {
        for (ShareholderType value : ShareholderType.values()) {
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
