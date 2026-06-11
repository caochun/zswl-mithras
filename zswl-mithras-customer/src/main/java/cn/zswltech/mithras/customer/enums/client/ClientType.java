package cn.zswltech.mithras.customer.enums.client;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @author junke
 */
public enum ClientType implements PullDown {
    /*法人*/
    CORPORATION("法人"),
    /*自然人*/
    NORMAL("自然人");

    ClientType(String display) {
        this.display = display;
    }

    private final String display;

    public String getDisplay() {
        return display;
    }

    public static ClientType of(String name) {
        for (ClientType value : ClientType.values()) {
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
