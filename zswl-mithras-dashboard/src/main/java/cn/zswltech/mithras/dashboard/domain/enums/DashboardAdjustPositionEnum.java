package cn.zswltech.mithras.dashboard.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public enum DashboardAdjustPositionEnum implements PullDown {
    DEPT_GENERAL_MANAGER_AID("部门总经理助理",new BigDecimal("1.8")),
    SENIOR_PROJ_MANAGER("资深项目经理",new BigDecimal("1.8")),
    HIGH_PROJ_MANAGER("高级项目经理",new BigDecimal("1.4")),
    MIDDLE_PROJ_MANAGER("中级项目经理",new BigDecimal("1")),
    LOW_PROJ_MANAGER("初级项目经理",new BigDecimal("0.7")),
    VISIT("项目助理",BigDecimal.ZERO),
    ;

    private final String display;
    private final BigDecimal multiple;

    @Override
    public String display() {
        return this.display;
    }

    public static DashboardAdjustPositionEnum find(String name) {
        for (DashboardAdjustPositionEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public static DashboardAdjustPositionEnum findByDisplay(String display) {
        for (DashboardAdjustPositionEnum item : values()) {
            if (item.getDisplay().equals(display)) {
                return item;
            }
        }
        return null;
    }
}
