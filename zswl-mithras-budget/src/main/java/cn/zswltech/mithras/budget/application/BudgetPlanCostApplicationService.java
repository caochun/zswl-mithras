package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostDetailListRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostListRSP;

import java.util.List;

public interface BudgetPlanCostApplicationService {

    PageR<BudgetPlanCostListRSP> pageList(BudgetPlanCostListREQ req);

    List<BudgetPlanCostDetailListRSP> detailList(Long budgetPlanCostId);
}
