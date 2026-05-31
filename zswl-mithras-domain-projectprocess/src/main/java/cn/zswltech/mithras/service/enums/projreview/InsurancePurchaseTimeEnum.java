package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 保险购买时间枚举
 */
public enum InsurancePurchaseTimeEnum implements PullDown {

    BEFORE_LOAN_DISBURSEMENT("放款前"),

    AFTER_LOAN_DISBURSEMENT("放款后");

    public final String display;

    InsurancePurchaseTimeEnum(String display) {
        this.display = display;
    }

    public static InsurancePurchaseTimeEnum of(String code) {
        for (InsurancePurchaseTimeEnum value : InsurancePurchaseTimeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @JsonValue
    public String getDisplay() {
        return display;
    }

    @Override
    public String display() {
        return display;
    }
}
