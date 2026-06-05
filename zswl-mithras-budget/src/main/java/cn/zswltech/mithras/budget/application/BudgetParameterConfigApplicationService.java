package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.dto.budget.BudgetParameterConfigListREQ;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigListRSP;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigModifyREQ;

import java.util.List;

public interface BudgetParameterConfigApplicationService {

    void modify(BudgetParameterConfigModifyREQ req);

    List<BudgetParameterConfigListRSP> list(BudgetParameterConfigListREQ req);
}
