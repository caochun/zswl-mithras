package cn.zswltech.mithras.service.enums.riskcontrol;
import cn.zswltech.mithras.common.enums.PullDown;

public enum RiskControlAssertEnum implements PullDown {

    EFFECT("生效"),
    NOT_EFFECT("不生效"),
    ;

    public String display;


    RiskControlAssertEnum(String display) {
        this.display = display;
    }

    public static RiskControlAssertEnum of(String name) {
        for (RiskControlAssertEnum value : RiskControlAssertEnum.values()) {
            if (value.name().equals(name)) {
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
