package cn.zswltech.mithras.budget.application.port;

import java.util.List;

public interface BudgetKpiFactPort {

    List<BudgetPerformanceTargetSnapshot> listDepartmentTargets(Integer year);
}
