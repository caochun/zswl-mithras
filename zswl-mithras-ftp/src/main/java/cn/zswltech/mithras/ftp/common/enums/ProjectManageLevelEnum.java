package cn.zswltech.mithras.ftp.common.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectManageLevelEnum implements PullDown {
    CITY("市级"),
    DISTRICT("区县级"),
    TOWN("镇级");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static ProjectManageLevelEnum getByName(String name) {
        for (ProjectManageLevelEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
