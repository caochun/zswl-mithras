package cn.zswltech.mithras.service.enums.capital;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/6/6
 * @description
 */
@Getter
@AllArgsConstructor
public enum FinanceCollectionWriteOffOrderEnum implements PullDown {
    FINANCE_FUND("融资款", 1),
    DEPOSIT_REFUND("保证金退款", 2);

    private final String display;
    private final int sort;

    @Override
    public String display() {
        return this.display;
    }

    public static FinanceCashFlowItemEnum transform(String name) {
        if (Objects.equals(FINANCE_FUND.name(), name)) {
            return FinanceCashFlowItemEnum.FINANCE_FUND;
        }
        if (Objects.equals(DEPOSIT_REFUND.name(), name)) {
            return FinanceCashFlowItemEnum.DEPOSIT_RETURN;
        }
        return null;
    }
}
