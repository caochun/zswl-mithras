package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.common.enums.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RentPaymentMethodTypeEnum implements PullDown {

    EQUAL_PRINCIPAL("等额本金"),
    EQUAL_RENT("等额租金"),
    OTHER("其他：详见附表");

    public String display;

    RentPaymentMethodTypeEnum(String display) {
        this.display = display;
    }

    public static RentPaymentMethodTypeEnum of(String code) {
        for (RentPaymentMethodTypeEnum value : RentPaymentMethodTypeEnum.values()) {
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
