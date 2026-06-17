package cn.zswltech.mithras.budget.application.port;

import lombok.Data;

@Data
public class BudgetFinanceProjectProfitSnapshot {

    private Long assessDeptId;

    private Long riskBalanceBeginYear;

    private Long totalRiskThisYear;

    private Long totalCostThisYear;

    private Long totalAdditionalTaxThisYear;
}
