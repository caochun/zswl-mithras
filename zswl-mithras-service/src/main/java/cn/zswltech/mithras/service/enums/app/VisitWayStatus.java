package cn.zswltech.mithras.service.enums.app;

import cn.zswltech.mithras.common.enums.PullDown;

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
