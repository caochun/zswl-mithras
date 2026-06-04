package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectType implements PullDown {
    PUBLIC_UTILITIES("公用事业类"),
    //    PEOPLE_CONSUME("民生消费类"),
    STATE_OWNED_ENTERPRISE("省内国（央）企"),

    OTHER("其他");

    public String display;

    ProjectType(String display) {
        this.display = display;
    }

    public static ProjectType of(String code) {
        for (ProjectType value : ProjectType.values()) {
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
