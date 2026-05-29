package cn.zswltech.mithras.service.enums.fund.liquidity;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: chenyifei
 */
@AllArgsConstructor
@Getter
public enum SettingTimeEnum implements PullDown {
    /**
     *
     */
    WITHIN_THREE_DAYS("3天以内",0),
    THREE_TO_TEN_DAYS("3-10天",1),
    TEN_TO_THIRTY_DAYS("10-30天",2),
    ONE_TO_THREE_MONTHS("1-3月",3),
    MORE_THAN_THREE_MONTHS("3个月以上",4),
    ;

    private String display;
    private Integer sort;

    public static SettingTimeEnum findByDisplay(String display) {
        for (SettingTimeEnum item : values()) {
            if (item.getDisplay().equals(display)) {
                return item;
            }
        }
        return null;
    }

    public static SettingTimeEnum findByName(String name) {
        for (SettingTimeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }


    @Override
    public String display() {
        return display;
    }
}
