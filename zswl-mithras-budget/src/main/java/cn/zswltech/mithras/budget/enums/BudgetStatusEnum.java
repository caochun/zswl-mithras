package cn.zswltech.mithras.budget.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/4/13
 * @description
 */
@AllArgsConstructor
@Getter
public enum BudgetStatusEnum implements PullDown {
    COLLECTING("收集中"),
    COLLECT_FINISH("收集完成"),
    CONFIRM("已确认");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
