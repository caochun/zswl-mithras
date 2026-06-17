package cn.zswltech.mithras.budget.application.port;

import lombok.Data;

@Data
public class BudgetContractPaymentFactSnapshot {
    private Long contractId;
    private Long belongDeptId;
    private Long payAmount;
    private Long firstRentAmount;
    private Long principalAmount;
}
