package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FinancingFundMethodTypeEnum implements PullDown {

    ONE_PAYMENT("一次性支付"),
    INSTALLMENT_PAYMENT("分次支付");

    public String display;

    FinancingFundMethodTypeEnum(String display) {
        this.display = display;
    }

    public static FinancingFundMethodTypeEnum of(String code) {
        for (FinancingFundMethodTypeEnum value : FinancingFundMethodTypeEnum.values()) {
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
