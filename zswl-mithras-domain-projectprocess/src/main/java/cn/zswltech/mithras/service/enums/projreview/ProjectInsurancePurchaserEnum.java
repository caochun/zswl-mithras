package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectInsurancePurchaserEnum implements PullDown {

    NOT("无需购买保险"),

    LESSEE_BUY("承租人购买保险"),

    OUR_COMPANY_BUY("我司购买保险");

    public String display;

    ProjectInsurancePurchaserEnum(String display) {
        this.display = display;
    }

    public static ProjectInsurancePurchaserEnum of(String code) {
        for (ProjectInsurancePurchaserEnum value : ProjectInsurancePurchaserEnum.values()) {
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
