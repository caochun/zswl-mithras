package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @author luyi
 */
public enum MarriageType implements PullDown {

    MARRIED("已婚"), UNMARRIED("未婚"), DIVORCED("离异"), WIDOWHOOD("丧偶");

    MarriageType(String display) {
        this.display = display;
    }

    public final String display;

    public static MarriageType of(String code) {
        for (MarriageType value : MarriageType.values()) {
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
