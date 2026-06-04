package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectPolicyRequireEnum implements PullDown {

    OUR_COMPANY_FIRST("我司为被保险人或第一受益人"),

    OTHER("其他");

    public String display;

    ProjectPolicyRequireEnum(String display) {
        this.display = display;
    }

    public static ProjectPolicyRequireEnum of(String code) {
        for (ProjectPolicyRequireEnum value : ProjectPolicyRequireEnum.values()) {
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
