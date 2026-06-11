package cn.zswltech.mithras.customer.enums.client;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/29 17:37
 */
public enum DomesticOrAbroad implements PullDown {
    /**
     * 境内
     */
    DOMESTIC("境内"),
    /**
     * 境外
     */
    ABROAD("境外");
    DomesticOrAbroad(String display) {
        this.display = display;
    }
    private final String display;

    public String getDisplay() {
        return display;
    }
    public static DomesticOrAbroad of(String name) {
        for (DomesticOrAbroad value : DomesticOrAbroad.values()) {
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
