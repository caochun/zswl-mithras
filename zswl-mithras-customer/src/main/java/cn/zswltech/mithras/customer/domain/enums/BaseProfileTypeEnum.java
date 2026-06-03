package cn.zswltech.mithras.customer.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

public enum BaseProfileTypeEnum implements PullDown {

    commitment("资料真实性承诺书"),

    corporation("法定代表人证明书"),

    honest("廉洁自律告知书")
    ;

    BaseProfileTypeEnum(String display) {
        this.display = display;
    }

    public final String display;

    public String getDisplay() {
        return display;
    }

    @Override
    public String display() {
        return display;
    }

    public static BaseProfileTypeEnum ofName(String name) {
        for (BaseProfileTypeEnum baseProfileTypeEnum : BaseProfileTypeEnum.values()) {
            if (baseProfileTypeEnum.name().equals(name)) {
                return baseProfileTypeEnum;
            }
        }
        return null;
    }
}
