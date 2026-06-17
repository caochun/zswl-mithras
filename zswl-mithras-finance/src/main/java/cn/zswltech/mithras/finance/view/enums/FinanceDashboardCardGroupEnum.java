package cn.zswltech.mithras.finance.view.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FinanceDashboardCardGroupEnum {
    FUND_FINANCE_REPAY(1, "还本付息"),
    FUND_FINANCE_LOAN(4, "融资情况（存量）"),
    FUND_FINANCE_CREDIT(2, "授信情况"),
    FUND_FINANCE_LOAN_THIS_YEAR(5, "融资情况（本年新增）"),
    FUND_FINANCE_LOAN_THIS_MONTH(6, "融资情况（本月新增）"),
    FOND_FINANCE_COST_FOUNDS(3, "资金成本");

    private final Integer sort;
    private final String display;

    public static FinanceDashboardCardGroupEnum ofName(String name) {
        for (FinanceDashboardCardGroupEnum anEnum : FinanceDashboardCardGroupEnum.values()) {
            if (anEnum.name().equals(name)) {
                return anEnum;
            }
        }
        return null;
    }
}
