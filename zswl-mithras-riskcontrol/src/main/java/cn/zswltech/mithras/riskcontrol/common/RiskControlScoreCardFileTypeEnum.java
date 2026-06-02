package cn.zswltech.mithras.riskcontrol.common;
import cn.zswltech.mithras.service.config.enumscan.PullDown;

public enum RiskControlScoreCardFileTypeEnum implements PullDown {
    ECONOMIC_DATA("经济数据"),
   ;


    public String display;

    RiskControlScoreCardFileTypeEnum(String display) {
        this.display = display;
    }

    public static RiskControlScoreCardFileTypeEnum of(String name) {
        for (RiskControlScoreCardFileTypeEnum value : RiskControlScoreCardFileTypeEnum.values()) {
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
