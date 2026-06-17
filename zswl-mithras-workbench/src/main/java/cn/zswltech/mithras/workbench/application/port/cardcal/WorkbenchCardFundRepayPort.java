package cn.zswltech.mithras.workbench.application.port.cardcal;

import cn.zswltech.mithras.workbench.application.cardcal.model.WorkbenchFundRepayCashFlow;

import java.time.LocalDate;
import java.util.List;

public interface WorkbenchCardFundRepayPort {
    List<WorkbenchFundRepayCashFlow> listRepayCashFlows(LocalDate start, LocalDate end);
}
