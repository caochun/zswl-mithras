package cn.zswltech.mithras.service.enums.budget;

import cn.zswltech.mithras.common.enums.PullDown;
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
