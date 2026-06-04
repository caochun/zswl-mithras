package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EarnestMoneyCollectTypeEnum implements PullDown {

    CHARGE_PROPORTIONALLY("放款前按投放比例收取"),
    OTHER("其他");

    public String display;

    EarnestMoneyCollectTypeEnum(String display) {
        this.display = display;
    }

    public static EarnestMoneyCollectTypeEnum of(String code) {
        for (EarnestMoneyCollectTypeEnum value : EarnestMoneyCollectTypeEnum.values()) {
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
