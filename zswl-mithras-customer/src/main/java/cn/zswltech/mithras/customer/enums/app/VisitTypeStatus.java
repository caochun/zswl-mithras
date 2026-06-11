package cn.zswltech.mithras.customer.enums.app;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @author luyi
 */
public enum VisitTypeStatus implements PullDown {

    CLIENT_VISIT("客户拜访"),
    CHANNEL_VISIT("渠道拜访")
    ;

    VisitTypeStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static VisitTypeStatus of(String code) {
        for (VisitTypeStatus value : VisitTypeStatus.values()) {
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
