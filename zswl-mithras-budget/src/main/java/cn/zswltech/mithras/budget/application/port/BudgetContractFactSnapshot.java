package cn.zswltech.mithras.budget.application.port;

import lombok.Data;

@Data
public class BudgetContractFactSnapshot {

    private Long id;

    private Long bizDeptId;

    private Long projReviewId;

    private String projCode;
}
