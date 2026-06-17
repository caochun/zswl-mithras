package cn.zswltech.mithras.dashboard.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2024/6/19
 * @description
 */
@AllArgsConstructor
@Getter
public enum DashboardCardGroupEnum implements PullDown {
    PROJECT_VIEW_STAGE_ESTABLISH(0, "立项阶段"),
    PROJECT_VIEW_STAGE_REVIEW(0, "评审阶段"),
    PROJECT_VIEW_STAGE_REVIEW_NO_CONTRACT(0, "评审通过未创建合同"),
    PROJECT_VIEW_STAGE_CONTRACT(0, "签约阶段"),
    PROJECT_VIEW_STAGE_PREPARE_PAYMENT(0, "付款阶段"),
    PROJECT_VIEW_STAGE_PAYMENT(0, "投放阶段"),
    PROJECT_VIEW_STAGE_REPAYMENT(0, "还款阶段"),
    PROJECT_VIEW_STAGE_REVIEW_NO_LEGAL_REPORT(0, "待出具合规意见书"),
    PROJECT_VIEW_INFO_SETTLE_IN_THREE_MONTH(0, "3个月内结清项目"),
    PROJECT_VIEW_INFO_OVERDUE(0, "存在逾期项目"),
    PROJECT_VIEW_INFO_RENT_IN_MONTH(0, "本月应收租金"),
    CLIENT_ALL(0, "所有客户"),
    CLIENT_SURVIVAL(0, "存续客户"),
    CLIENT_THREE_MONTH_SETTLE(0, "3个月内结清客户"),
    CLIENT_SETTLE(0, "已结清客户"),
    CLIENT_OVERDUE(0, "逾期客户"),
    CLIENT_AFTER_LEASE(0, "租后管理"),
    PROJECT_VIEW_FINANCE_RENT_IN_MONTH(0, "本月应收租金"),
    PROJECT_VIEW_FINANCE_PLEDGE(0, "项目质押/监管情况"),
    PROJECT_VIEW_FINANCE_OVERDUE(0, "存在逾期项目"),
    PROJECT_VIEW_FINANCE_PROVISION(0, "剩余本金与拨备"),
    PROJECT_VIEW_FINANCE_NO_SETTLE(0, "已投放未结清"),
    FUND_FINANCE_REPAY(1, "还本付息"),
    FUND_FINANCE_LOAN(4, "融资情况（存量）"),
    FUND_FINANCE_CREDIT(2, "授信情况"),
    FUND_FINANCE_LOAN_THIS_YEAR(5, "融资情况（本年新增）"),
    FUND_FINANCE_LOAN_THIS_MONTH(6, "融资情况（本月新增）"),
    FOND_FINANCE_COST_FOUNDS(3, "资金成本");

    private final Integer sort;
    private final String display;

    public static DashboardCardGroupEnum ofName(String name) {
        for (DashboardCardGroupEnum anEnum : DashboardCardGroupEnum.values()) {
            if (anEnum.name().equals(name)) {
                return anEnum;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
