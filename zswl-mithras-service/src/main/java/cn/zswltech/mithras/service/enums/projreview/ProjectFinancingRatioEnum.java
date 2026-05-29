package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.common.enums.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectFinancingRatioEnum implements PullDown {

    EIGHTY(800000),
    EIGHTY_FIVE(850000),
    NINETY(900000),
    NINETY_FIVE(950000),
    OTHER(0);

    public Integer display;

    ProjectFinancingRatioEnum(Integer display) {
        this.display = display;
    }

    public static ProjectFinancingRatioEnum of(String code) {
        for (ProjectFinancingRatioEnum value : ProjectFinancingRatioEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @JsonValue
    public Integer getDisplay() {
        return display;
    }

    @Override
    public String display() {
        return String.valueOf(display);
    }
}
