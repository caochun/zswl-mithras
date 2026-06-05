package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.dto.budget.BudgetExamineBenefitListREQ;
import cn.zswltech.mithras.dto.budget.BudgetExamineBenefitListRSP;
import cn.zswltech.mithras.dto.budget.BudgetExamineBenefitModifyREQ;

import java.util.List;

public interface BudgetExamineBenefitApplicationService {

    void modify(List<BudgetExamineBenefitModifyREQ> req);

    List<BudgetExamineBenefitListRSP> list(BudgetExamineBenefitListREQ req);
}
