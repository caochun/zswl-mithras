package cn.zswltech.mithras.budget.application.port;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BudgetFinanceFactPort {

    boolean hasSubjectBalanceAssist(Integer year, Integer month);

    Map<Long, Map<String, BigDecimal>> listMonthDeptRiskValues(Integer year, Integer month);

    List<BudgetFinanceProjectProfitSnapshot> listProjectProfitDetails(Integer year, Integer month);
}
