package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RentPaymentMethodRateEnum implements PullDown {

    MONTH("按月"),
    QUARTER("按季"),
    HALF_YEAR("按半年"),
    OTHER("其他：详见附表");

    public String display;

    RentPaymentMethodRateEnum(String display) {
        this.display = display;
    }

    public static RentPaymentMethodRateEnum of(String code) {
        for (RentPaymentMethodRateEnum value : RentPaymentMethodRateEnum.values()) {
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
