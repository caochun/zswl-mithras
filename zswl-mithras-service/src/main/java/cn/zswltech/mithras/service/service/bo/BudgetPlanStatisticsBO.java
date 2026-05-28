package cn.zswltech.mithras.service.service.bo;

import lombok.Data;

/**
 * @author dingqi
 * @date 2025/7/28
 * @description
 */
@Data
public class BudgetPlanStatisticsBO {
    private Long endOfLastPeriodBalanceTotal = 0L;
    private Long newActualPayThisPeriodTotal = 0L;
    private Integer priorityAverageIrr = 0;
    private Long incomeWithoutTaxTotal = 0L;
    private Long profitTotal = 0L;
    private Long profitWithoutExpenseTotal = 0L;
    private Long endOfThisPeriodBalanceTotal = 0L;
}
