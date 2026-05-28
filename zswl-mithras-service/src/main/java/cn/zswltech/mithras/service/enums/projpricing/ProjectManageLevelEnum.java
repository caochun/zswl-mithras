package cn.zswltech.mithras.service.enums.projpricing;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/2/26
 * @description
 */
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
