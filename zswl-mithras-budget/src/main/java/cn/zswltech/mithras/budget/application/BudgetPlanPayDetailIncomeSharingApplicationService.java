package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailDynamicTableRSP;

public interface BudgetPlanPayDetailIncomeSharingApplicationService {

    BudgetPlanPayDetailDynamicTableRSP getIncomeSharingDynamicTable(Long budgetPlanPayDetailId);
}
