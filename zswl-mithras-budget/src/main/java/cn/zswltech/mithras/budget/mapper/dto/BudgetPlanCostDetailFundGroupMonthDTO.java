package cn.zswltech.mithras.budget.mapper.dto;

import lombok.Data;

/**
 * @author dingqi
 * @date 2025/6/26
 * @description
 */
@Data
public class BudgetPlanCostDetailFundGroupMonthDTO {
    private Integer year;
    private Integer month;
    private Long repayAmountSum;
    private Long principalSum;
    private Long interestSum;
}
