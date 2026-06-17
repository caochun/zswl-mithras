package cn.zswltech.mithras.budget.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BudgetApprovalStatusEnum implements PullDown {
    UN_SUBMIT("未提交"),
    COMMIT("审批中"),
    CANCEL("取消流程"),
    REJECT("审批拒绝"),
    PASS("审批通过");

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
