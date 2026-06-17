package cn.zswltech.mithras.budget.application.port;

import java.util.Collection;
import java.util.List;

public interface BudgetContractFactPort {

    List<BudgetContractFactSnapshot> listByIds(Collection<Long> contractIds);
}
