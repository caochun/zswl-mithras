package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectPolicyTypeEnum implements PullDown {

    PROPERTY_ALL_RISKS_INSURANCE("财产一切险/财产综合险"),

    OTHER("其他");

    public String display;

    ProjectPolicyTypeEnum(String display) {
        this.display = display;
    }

    public static ProjectPolicyTypeEnum of(String code) {
        for (ProjectPolicyTypeEnum value : ProjectPolicyTypeEnum.values()) {
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
