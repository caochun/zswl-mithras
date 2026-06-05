package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeekReportDetailDeptConfirmInfoRSP;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeekReportDetailModifyREQ;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportDetailAddREQ;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportDetailListREQ;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportDetailListRSP;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportDetailStatisticsRSP;

import java.util.List;

public interface BudgetPlanPayWeeklyReportDetailApplicationService {

    BudgetPlanPayWeeklyReportDetailStatisticsRSP statisticsMonth(BudgetPlanPayWeeklyReportDetailListREQ req);

    PageR<BudgetPlanPayWeeklyReportDetailListRSP> pageListMonth(BudgetPlanPayWeeklyReportDetailListREQ req);

    void addDetailMonth(BudgetPlanPayWeeklyReportDetailAddREQ req);

    void modifyDetailMonth(BudgetPlanPayWeekReportDetailModifyREQ req);

    List<BudgetPlanPayWeeklyReportDetailListRSP> listMonthDetailContract(Long detailId);

    List<BudgetPlanPayWeekReportDetailDeptConfirmInfoRSP> listBizDeptConfirmInfo(Long budgetPlanPayId);

    void removeBatch(MultiplePkREQ req);
}
