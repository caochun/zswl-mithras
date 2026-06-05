package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportListREQ;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportListRSP;

public interface BudgetPlanPayWeeklyReportApplicationService {

    PageR<BudgetPlanPayWeeklyReportListRSP> pageList(BudgetPlanPayWeeklyReportListREQ req);

    BudgetPlanPayWeeklyReportListRSP planInfo(Long id);
}
