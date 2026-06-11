package cn.zswltech.mithras.kpi.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 租赁物类型枚举
 */
@AllArgsConstructor
@Getter
public enum KpiLeaseItemTypeEnum implements PullDown {
    INCLUDE_SHIP("含船"),
    EXCLUDE_SHIP("不含船");

    private final String display;


    public static KpiLeaseItemTypeEnum find(String name) {
        for (KpiLeaseItemTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
