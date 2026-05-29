package cn.zswltech.mithras.service.enums.policy;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @create: 2023-06-16
 **/
public enum PolicyStatusEnum implements PullDown {
    EFFECT("已确认"),
    NEW_ADD("新增")
    ;
    PolicyStatusEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static PolicyStatusEnum of(String code) {
        for (PolicyStatusEnum value : PolicyStatusEnum.values()) {
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
