package cn.zswltech.mithras.workbench.application.port;

import cn.zswltech.mithras.workbench.application.WorkbenchFundsLiquidityCollection;
import cn.zswltech.mithras.workbench.application.WorkbenchFundsLiquidityRepayCashFlow;

import java.time.LocalDate;
import java.util.List;

public interface WorkbenchFundsLiquidityPort {
    List<WorkbenchFundsLiquidityCollection> listRentCollections(LocalDate start, LocalDate end);

    List<WorkbenchFundsLiquidityRepayCashFlow> listRepayCashFlows(LocalDate start, LocalDate end);
}
