package cn.zswltech.mithras.fund.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/17 10:20
 */
public enum DirectFinancingExpenseType implements PullDown {
    //托管费，担保费，财务顾问费，信用评估费，贷款服务费，审计费，其他
    CUSTODY_FEE("托管费"),
    GUARANTEE_FEE("担保费"),
    FINANCIAL_ADVISORY_FEE("财务顾问费"),
    CREDIT_ASSESSMENT_FEE("信用评估费"),
    LOAN_SERVICE_FEE("贷款服务费"),
    AUDIT_FEE("审计费"),
    OTHER("其他"),
    ;
    private final String display;

    DirectFinancingExpenseType(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
