package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitCreateREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitListRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitProcessREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitProcessRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitRemoveREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitSummaryOtherREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitSummaryOtherRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitSummaryREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanProfitSummaryRSP;

import java.util.List;

public interface BudgetPlanProfitApplicationService {

    Long createAndInit(BudgetPlanProfitCreateREQ req);

    PageR<BudgetPlanProfitListRSP> pageList(BudgetPlanProfitListREQ req);

    BudgetPlanProfitRSP info(Long id);

    void delete(BudgetPlanProfitRemoveREQ req);

    BudgetPlanProfitSummaryRSP summary(BudgetPlanProfitSummaryREQ req);

    List<BudgetPlanProfitSummaryOtherRSP> summaryOther(BudgetPlanProfitSummaryOtherREQ req);

    void confirm(Long budgetPlanProfitId);

    List<BudgetPlanProfitProcessRSP> process(BudgetPlanProfitProcessREQ req);

    void notifyCreatePlanPay(Long budgetPlanProfitId);
}
