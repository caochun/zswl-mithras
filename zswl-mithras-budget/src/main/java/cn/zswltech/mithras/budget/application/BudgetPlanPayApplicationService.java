package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayListRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayRSP;

public interface BudgetPlanPayApplicationService {

    PageR<BudgetPlanPayListRSP> pageList(BudgetPlanPayListREQ req);

    BudgetPlanPayRSP planInfo(Long id);
}
