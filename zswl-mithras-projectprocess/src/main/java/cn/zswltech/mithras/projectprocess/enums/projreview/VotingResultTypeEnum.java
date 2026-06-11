package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VotingResultTypeEnum implements PullDown {

    CONSENT("同意"),
    CONDITIONAL_CONSENT("有条件同意"),
    VETO("否决");

    public String display;

    VotingResultTypeEnum(String display) {
        this.display = display;
    }

    public static VotingResultTypeEnum of(String code) {
        for (VotingResultTypeEnum value : VotingResultTypeEnum.values()) {
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
