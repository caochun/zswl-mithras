package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResolutionTypeRateEnum implements PullDown {

    FINANCIAL_LEASING_BUSINESS("融资租赁业务"),
    JOINT_AND_SEVERAL_LIABILITY_GUARANTEE("连带责任保证担保"),
    GUARANTEE_MORTGAGE("抵押担保"),
    GUARANTEE_PLEDGE("质押担保"),
    OTHER("其他");

    public String display;

    ResolutionTypeRateEnum(String display) {
        this.display = display;
    }

    public static ResolutionTypeRateEnum of(String code) {
        for (ResolutionTypeRateEnum value : ResolutionTypeRateEnum.values()) {
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
