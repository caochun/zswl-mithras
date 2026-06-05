package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.IdREQ;
import cn.zswltech.mithras.dto.budget.BudgetExamineAddREQ;
import cn.zswltech.mithras.dto.budget.BudgetExamineListREQ;
import cn.zswltech.mithras.dto.budget.BudgetExamineListRSP;
import cn.zswltech.mithras.dto.budget.BudgetExamineRemoveREQ;

public interface BudgetExamineApplicationService {

    void add(BudgetExamineAddREQ req);

    PageR<BudgetExamineListRSP> pageList(BudgetExamineListREQ req);

    void remove(BudgetExamineRemoveREQ req);

    void submit(IdREQ req);

    BudgetExamineListRSP detail(Long id);
}
