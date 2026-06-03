package cn.zswltech.mithras.budget.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/5/27
 * @description
 */
@Getter
@AllArgsConstructor
public enum BudgetPlanCalculateStatusEnum implements PullDown {
    DOING("计算中"),
    SUCCESS("计算成功"),
    FAILURE("计算失败")
    ;
    private final String display;

    @Override
    public String display() {
        return display;
    }
}
