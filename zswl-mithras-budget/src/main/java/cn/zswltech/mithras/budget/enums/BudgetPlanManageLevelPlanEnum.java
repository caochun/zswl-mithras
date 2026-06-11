package cn.zswltech.mithras.budget.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BudgetPlanManageLevelPlanEnum implements PullDown {
    CITY("市级"),
    COUNTY("区县级"),
    TOWN("镇级");

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
