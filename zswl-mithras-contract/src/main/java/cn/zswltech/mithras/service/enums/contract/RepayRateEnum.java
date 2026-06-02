package cn.zswltech.mithras.service.enums.contract;


import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum RepayRateEnum implements PullDown {

    MONTH("按月", "1"),
    DOUBLE_MONTH("按双月", "2"),
    QUARTER("按季", "3"),
    HALF_YEAR("按半年", "6"),
    YEAR("按年", "12"),
    LRREGULAR("不规则分期", null),
    NON_STAGES("非分期", "0")
    ;


    public final String display;
    public final String financialSystemCode;

    public static RepayRateEnum of(String code) {
        for (RepayRateEnum value : RepayRateEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static boolean isByRule(String name) {
        // 是否按规则还款
        return !Objects.equals(name, RepayRateEnum.NON_STAGES.name()) && !Objects.equals(name, RepayRateEnum.LRREGULAR.name());
    }

    public static int getRuleMonth(String name) {
        // 是否按规则还款
        int month = 0;
        if(MONTH.name().equals(name)){
            month = 1;
        } else if(DOUBLE_MONTH.name().equals(name)){
            month = 2;
        } else if (QUARTER.name().equals(name)){
            month = 3;
        } else if (HALF_YEAR.name().equals(name)){
            month = 6;
        } else if(YEAR.name().equals(name)){
            month = 12;
        }
        return month;
    }

    @Override
    public String display() {
        return display;
    }
}
