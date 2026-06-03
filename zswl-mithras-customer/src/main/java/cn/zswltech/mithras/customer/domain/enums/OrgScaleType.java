package cn.zswltech.mithras.customer.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @author junke
 */
public enum OrgScaleType implements PullDown {
    TINY("微型"), SMALL("小型"), MIDDLE("中型"), BIG("大型"), X("未知"),
    NO_REQUIRE("无需划型"),
    ;

    OrgScaleType(String display) {
        this.display = display;
    }

    public final String display;

    public static OrgScaleType of(String code) {
        for (OrgScaleType value : OrgScaleType.values()) {
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
