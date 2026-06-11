package cn.zswltech.mithras.budget.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@AllArgsConstructor
@Getter
public enum BudgetPlanPayFundPlanEnum implements PullDown {
    BRING_INTO("纳入"),
    BACKUP("备选"),
    NOT_BRING_INTO("未纳入");

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
