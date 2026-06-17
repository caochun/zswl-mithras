package cn.zswltech.mithras.budget.application.port;

import lombok.Data;

@Data
public class BudgetPerformanceTargetSnapshot {

    private Long belongDeptId;

    private String businessType;

    private Long advertisingAmount;

    private Long revenueTarget;

    private Long consultingFeeIncome;

    private Long interestIncome;

    private Long bizFee;

    private Long businessTripFee;

    private Long businessServeFee;

    private Long profitTarget;

    private Long beforeProfitTarget;
}
