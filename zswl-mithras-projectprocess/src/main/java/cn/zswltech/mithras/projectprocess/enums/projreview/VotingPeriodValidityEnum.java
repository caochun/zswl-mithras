package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 表决有效期
 **/
public enum VotingPeriodValidityEnum implements PullDown {

    SIX_MONTHS("6个月", 6),
    TWELVE_MONTHS("12个月", 12),
    ;

    public String display;
    public Integer month;

    VotingPeriodValidityEnum(String display, Integer month) {
        this.display = display;
        this.month = month;
    }

    public static VotingPeriodValidityEnum of(String code) {
        for (VotingPeriodValidityEnum value : VotingPeriodValidityEnum.values()) {
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

    public Integer getMonth() {
        return month;
    }

    @Override
    public String display() {
        return display;
    }
}
