package cn.zswltech.mithras.service.enums.riskcontrol;
import cn.zswltech.mithras.common.enums.PullDown;

public enum RiskControlEmotionEnum implements PullDown {
    FCC0000002QA("负面"),
    FCC0000002QF("中性"),
    FCC0000002Q9("正面"),
   ;


    public String display;

    RiskControlEmotionEnum(String display) {
        this.display = display;
    }

    public static RiskControlEmotionEnum of(String name) {
        for (RiskControlEmotionEnum value : RiskControlEmotionEnum.values()) {
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
