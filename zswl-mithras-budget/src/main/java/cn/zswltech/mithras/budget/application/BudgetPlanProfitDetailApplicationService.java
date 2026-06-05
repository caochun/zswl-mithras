package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.dto.budget.BudgetPlanProfitDetailFutureRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitDetailHistoryRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitDetailModifyREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitDetailREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitDetailRSP;

import java.util.List;

public interface BudgetPlanProfitDetailApplicationService {

    List<BudgetPlanProfitDetailHistoryRSP> listDetailHistoryRSP(Long budgetPlanProfitId);

    List<BudgetPlanProfitDetailFutureRSP> listDetailFutureRSP(Long budgetPlanProfitId);

    List<BudgetPlanProfitDetailRSP> deptDetail(BudgetPlanProfitDetailREQ req);

    void modifyDetail(BudgetPlanProfitDetailModifyREQ req);
}
