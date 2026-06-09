package cn.zswltech.mithras.workbench.application;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class WorkbenchFundsLiquidityRepayCashFlow {
    private LocalDate repayDate;
    private Long repayAmount;
    private String writeOffState;
}
