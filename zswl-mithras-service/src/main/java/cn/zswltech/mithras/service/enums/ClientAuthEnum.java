package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * 客户权限
 **/
public enum ClientAuthEnum implements PullDown {
    HIGH_SEAS("公海"), EXCLUSIVE("专属"), NO_AFFILIATION("无所属"), OTHER_EXCLUSIVE("他人专属");


    public String display;

    ClientAuthEnum(String display) {
        this.display = display;
    }

    public static ClientAuthEnum of(String name) {
        for (ClientAuthEnum value : ClientAuthEnum.values()) {
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
