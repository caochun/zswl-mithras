package cn.zswltech.mithras.budget.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@AllArgsConstructor
@Getter
public enum BudgetPlanPayYunYingFeedbackEnum implements PullDown {
    PSZ("评审中"),
    DTJZLW("待提交租赁物"),
    ZLWSHZ("租赁物审核中"),
    HTSHZ("合同审核中"),
    YZWHTDQY("已走完合同待签约"),
    YQYDFK("已签约待放款"),
    FKSHZ("放款审核中"),
    YZWFKDTF("已走完付款待投放");

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
