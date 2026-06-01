package cn.zswltech.mithras.service.enums.client;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @author luyi
 */
public enum ClientProjArchiveEnum implements PullDown {
    /**
     * 新建
     */
    COMPLETED_ARCHIVING("已完成归档"),
    INCOMPLETE_ARCHIVING("未完成归档"),
    NO_NEED_ARCHIVING("无需归档");

    ClientProjArchiveEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static ClientProjArchiveEnum of(String code) {
        for (ClientProjArchiveEnum value : ClientProjArchiveEnum.values()) {
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
