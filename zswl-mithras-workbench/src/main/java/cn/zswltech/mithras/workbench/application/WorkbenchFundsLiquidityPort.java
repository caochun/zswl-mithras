package cn.zswltech.mithras.workbench.application;

import java.time.LocalDate;
import java.util.List;

public interface WorkbenchFundsLiquidityPort {
    List<WorkbenchFundsLiquidityCollection> listRentCollections(LocalDate start, LocalDate end);

    List<WorkbenchFundsLiquidityRepayCashFlow> listRepayCashFlows(LocalDate start, LocalDate end);
}
