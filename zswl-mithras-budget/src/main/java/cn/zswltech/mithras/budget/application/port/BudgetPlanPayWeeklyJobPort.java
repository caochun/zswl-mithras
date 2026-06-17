package cn.zswltech.mithras.budget.application.port;

import java.time.LocalDate;

public interface BudgetPlanPayWeeklyJobPort {

    void createBudgetPlanPayWeekly(LocalDate targetDate);
}
