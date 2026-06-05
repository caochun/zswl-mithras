package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailNotMonthCashFlowRSP;

import java.io.InputStream;
import java.util.List;

public interface BudgetPlanPayDetailCashFlowApplicationService {

    List<BudgetPlanPayDetailNotMonthCashFlowRSP> listCashFlowByDetailId(Long budgetPlanPayDetailId);

    List<BudgetPlanPayDetailNotMonthCashFlowRSP> parseFromExcel(InputStream inputStream);
}
