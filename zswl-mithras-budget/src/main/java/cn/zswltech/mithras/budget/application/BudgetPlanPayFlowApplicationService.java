package cn.zswltech.mithras.budget.application;

import java.util.Map;

public interface BudgetPlanPayFlowApplicationService {

    Map<Long, String> createYearFlow(Long planId);

    Map<Long, String> createMonthFlow(Long planId);
}
