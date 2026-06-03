package cn.zswltech.mithras.dashboard.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 */

@Getter
@AllArgsConstructor
public enum PayInfoQueryDimensionEnum implements PullDown {

    CONTRACT("合同"),
    RECEIPT("借据"),
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }

    public static PayInfoQueryDimensionEnum find(String name) {
        for (PayInfoQueryDimensionEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
