package cn.zswltech.mithras.service.enums.afterlease;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.config.enumscan.PullDown;

public enum AfterLeaseCheckTermEnum implements PullDown {
    MONTH("月", 30, 1),
    TWO_MONTH("双月", 60, 2),
    QUARTER("季度", 90, 3),
    HALF_A_YEAR("半年", 180, 6),
    YEAR("年", 360, 12);

    AfterLeaseCheckTermEnum(String display, Integer term, Integer month) {
        this.display = display;
        this.term = term;
        this.month = month;
    }

    public final String display;
    public final Integer term;
    public final Integer month;


    public static AfterLeaseCheckTermEnum of(String code) {
        for (AfterLeaseCheckTermEnum value : AfterLeaseCheckTermEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
    public static AfterLeaseCheckTermEnum ofTerm(Integer term) {
        for (AfterLeaseCheckTermEnum value : AfterLeaseCheckTermEnum.values()) {
            if (ObjectUtil.equals(value.term, term)) {
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
