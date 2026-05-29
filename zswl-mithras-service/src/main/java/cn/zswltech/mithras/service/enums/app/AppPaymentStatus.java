package cn.zswltech.mithras.service.enums.app;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @author luyi
 */
public enum AppPaymentStatus implements PullDown {

    PAID("已放款"),
    NO_PAID("未放款")
    ;

    AppPaymentStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static AppPaymentStatus of(String code) {
        for (AppPaymentStatus value : AppPaymentStatus.values()) {
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
