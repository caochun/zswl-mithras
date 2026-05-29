package cn.zswltech.mithras.service.enums.contract;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description
 */
@AllArgsConstructor
@Getter
public enum ContractOperationEnum implements PullDown {
    START_RENT("合同起租"),
//    NEW_PAYMENT("新增投放"),
    NEW_RECEIPT("新增借据(投放)"),
    CHANGE_LPR("合同变更-LPR调整"),
    CHANGE_REPAYMENT_IN_ADVANCE("合同变更-提前还款"),
    CHANGE_EXTENSION("合同变更-展期"),
    CHANGE_REPAYMENT_PLAN("合同变更-调整还款计划"),
    CHANGE_OTHER("合同变更-其他"),
    SETTLE_NORMAL("合同结清-正常结清"),
    SETTLE_IN_ADVANCE("合同结清-提前结清");

    private final String display;

    public static ContractOperationEnum find(String operation) {
        for (ContractOperationEnum item : values()) {
            if (Objects.equals(item.name(), operation)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
