package cn.zswltech.mithras.afterlease.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/11/10
 * @description
 */
@AllArgsConstructor
@Getter
public enum AfterLeaseCheckWayEnum implements PullDown {
    SITE("现场检查"),
    OFFSITE("非现场检查"),
    WITHOUT_CHECK("已结清无需检查")
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static AfterLeaseCheckWayEnum find(String name) {
        for (AfterLeaseCheckWayEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
