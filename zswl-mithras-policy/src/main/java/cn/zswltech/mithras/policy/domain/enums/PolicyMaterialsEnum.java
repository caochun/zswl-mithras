package cn.zswltech.mithras.policy.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @create: 2023-06-16
 **/
public enum PolicyMaterialsEnum implements PullDown {
    POLICY("保单资料"),
    ;
    PolicyMaterialsEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static PolicyMaterialsEnum of(String code) {
        for (PolicyMaterialsEnum value : PolicyMaterialsEnum.values()) {
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
