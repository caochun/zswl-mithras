package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.dto.budget.BudgetExaminePayPlanExecuteListREQ;
import cn.zswltech.mithras.dto.budget.BudgetExaminePayPlanExecuteListRSP;

import java.util.List;

public interface BudgetExaminePayPlanExecuteApplicationService {

    List<BudgetExaminePayPlanExecuteListRSP> list(BudgetExaminePayPlanExecuteListREQ req);
}
