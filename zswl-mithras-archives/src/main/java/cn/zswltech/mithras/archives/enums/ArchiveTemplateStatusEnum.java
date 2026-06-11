package cn.zswltech.mithras.archives.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @create: 2023-02-27
 **/
public enum ArchiveTemplateStatusEnum implements PullDown {
    ENABLE("启用"),
    DISABLED("禁用"),
;
    ArchiveTemplateStatusEnum(String display) {
        this.display = display;
    };

    public final String display;

    public static ArchiveTemplateStatusEnum of(String code) {
        for (ArchiveTemplateStatusEnum value : ArchiveTemplateStatusEnum.values()) {
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
