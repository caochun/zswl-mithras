package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MeetMinuteStatuesEnum implements PullDown {

    NEW("新建"),
    HOLD("暂存"),
    SUBMIT("提交"),
    EFFECT("生效"),
    ;

    public String display;

    MeetMinuteStatuesEnum(String display) {
        this.display = display;
    }

    public static MeetMinuteStatuesEnum of(String code) {
        for (MeetMinuteStatuesEnum value : MeetMinuteStatuesEnum.values()) {
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
