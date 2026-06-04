package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RentalStartMethodEnum implements PullDown {

    START_RENT_ONCE("一次性起租"),
    START_RENT_MANY("分次起租"),
    ;

    public String display;

    RentalStartMethodEnum(String display) {
        this.display = display;
    }

    public static RentalStartMethodEnum of(String code) {
        for (RentalStartMethodEnum value : RentalStartMethodEnum.values()) {
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
