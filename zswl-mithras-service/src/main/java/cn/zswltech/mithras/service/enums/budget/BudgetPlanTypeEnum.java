package cn.zswltech.mithras.service.enums.budget;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/4/13
 * @description
 */
@AllArgsConstructor
@Getter
public enum BudgetPlanTypeEnum implements PullDown {
    YEAR("年度计划", "YEAR"),
    HALF_OF_YEAR("半年度计划", "YEAR"),
    MONTH("月度计划", "MONTH"),
    MONTH_ADJUST("月度调整计划", "MONTH"),
    OTHER("其他计划", "YEAR");

    private final String display;
    private final String profitPeriodType;

    @Override
    public String display() {
        return this.display;
    }

    public static BudgetPlanTypeEnum findByName(String name) {
        for (BudgetPlanTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}
