package cn.zswltech.mithras.dashboard.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public enum DashboardOperationTermEnum implements PullDown {
    CURRENT_TERM("当期平均"),
    LAST_TERM("去年同期"),
    CURRENT_YEAR("本年平均"),
    LAST_YEAR("去年平均"),
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static DashboardOperationTermEnum find(String name) {
        for (DashboardOperationTermEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
