package cn.zswltech.mithras.customer.mobile.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @author luyi
 */
public enum VisitWayStatus implements PullDown {

    ON_SITE_VISIT("现场拜访"),
    FORGET_CHECK_IN("漏打卡")
    ;

    VisitWayStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static VisitWayStatus of(String code) {
        for (VisitWayStatus value : VisitWayStatus.values()) {
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
