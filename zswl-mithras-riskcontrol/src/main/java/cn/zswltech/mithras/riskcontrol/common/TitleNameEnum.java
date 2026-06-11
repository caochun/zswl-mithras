package cn.zswltech.mithras.riskcontrol.common;

import cn.zswltech.mithras.foundation.metadata.PullDown;

public enum TitleNameEnum implements PullDown {
    AREA("地区"),
    PROVINCE("省"),
    CITY("市"),
    EXECUTIVE_LEVEL("行政级别"),
    REGIONAL_LEVEL("区域级别");


    public String display;

    TitleNameEnum(String display) {
        this.display = display;
    }

    public static TitleNameEnum of(String name) {
        for (TitleNameEnum value : TitleNameEnum.values()) {
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
