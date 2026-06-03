package cn.zswltech.mithras.message.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

public enum MessageChannelEnum implements PullDown {
    /*法人*/
    PC("pc端"),
    /*自然人*/
    APP("移动端");

    MessageChannelEnum(String display) {
        this.display = display;
    }

    private final String display;

    public String getDisplay() {
        return display;
    }

    public static MessageChannelEnum of(String name) {
        for (MessageChannelEnum value : MessageChannelEnum.values()) {
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
