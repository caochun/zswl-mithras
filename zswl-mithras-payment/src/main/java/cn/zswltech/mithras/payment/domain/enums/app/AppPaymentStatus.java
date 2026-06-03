package cn.zswltech.mithras.payment.domain.enums.app;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

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
