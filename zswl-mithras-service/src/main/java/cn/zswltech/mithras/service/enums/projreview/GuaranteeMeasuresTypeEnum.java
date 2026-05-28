package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 担保措施
 **/
public enum GuaranteeMeasuresTypeEnum implements PullDown {

    LEGAL_PERSON_JOINT_AND_SEVERAL_LIABILITY_GUARANTEE("法人连带责任担保"),
    NATURAL_PERSON_JOINT_AND_SEVERAL_LIABILITY_GUARANTEE("自然人连带责任担保"),
    OTHER("其他");

    public String display;

    GuaranteeMeasuresTypeEnum(String display) {
        this.display = display;
    }

    public static GuaranteeMeasuresTypeEnum of(String code) {
        for (GuaranteeMeasuresTypeEnum value : GuaranteeMeasuresTypeEnum.values()) {
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
