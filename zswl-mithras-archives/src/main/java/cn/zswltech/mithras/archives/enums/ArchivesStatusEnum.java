package cn.zswltech.mithras.archives.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @create: 2023-02-27
 **/
public enum ArchivesStatusEnum implements PullDown {
    WAITE("待补充"),
    OVER("已归档"),
;
    ArchivesStatusEnum(String display) {
        this.display = display;
    };

    public final String display;

    public static ArchivesStatusEnum of(String code) {
        for (ArchivesStatusEnum value : ArchivesStatusEnum.values()) {
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
