package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.dto.budget.BudgetExamineBudgetExecuteListREQ;
import cn.zswltech.mithras.dto.budget.BudgetExamineBudgetExecuteListRSP;
import cn.zswltech.mithras.dto.budget.BudgetExamineBudgetExecuteModifyREQ;

import java.util.List;

public interface BudgetExamineBudgetExecuteApplicationService {

    void modify(List<BudgetExamineBudgetExecuteModifyREQ> req);

    List<BudgetExamineBudgetExecuteListRSP> list(BudgetExamineBudgetExecuteListREQ req);
}
