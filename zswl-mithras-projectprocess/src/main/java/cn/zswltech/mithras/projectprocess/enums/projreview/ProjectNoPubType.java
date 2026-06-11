package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectNoPubType implements PullDown {
    STATE_OWNED_ENTERPRISE("省内国（央）企"),
    OTHER("其他");

    public String display;
    ProjectNoPubType(String display) {
        this.display = display;
    }

    @JsonValue
    public String getDisplay() {
        return display;
    }

    public static ProjectNoPubType of(String code) {
        for (ProjectNoPubType value : ProjectNoPubType.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
