package cn.zswltech.mithras.budget.application.port;

import lombok.Data;

@Data
public class BudgetPaymentActualSnapshot {
    private Long contractId;
    private Long paidInAmount;
}
