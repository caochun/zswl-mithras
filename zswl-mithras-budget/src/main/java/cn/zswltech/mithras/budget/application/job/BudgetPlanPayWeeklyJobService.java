package cn.zswltech.mithras.budget.application.job;

import java.time.LocalDate;

public interface BudgetPlanPayWeeklyJobService {

    void createBudgetPlanPayWeekly(LocalDate targetDate);
}
