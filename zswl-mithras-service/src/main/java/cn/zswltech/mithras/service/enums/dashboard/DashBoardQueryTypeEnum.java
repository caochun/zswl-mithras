package cn.zswltech.mithras.service.enums.dashboard;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/10/23 09:15
 * @description
 */
@Getter
@AllArgsConstructor
public enum DashBoardQueryTypeEnum implements PullDown {
    PUBLIC_CATEGORY("公用组"),
    INDUSTRY_CATEGORY("产业组"),
    ALL("合计"),
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }

    public static DashBoardQueryTypeEnum transform(String name) {
        for (DashBoardQueryTypeEnum item : DashBoardQueryTypeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
