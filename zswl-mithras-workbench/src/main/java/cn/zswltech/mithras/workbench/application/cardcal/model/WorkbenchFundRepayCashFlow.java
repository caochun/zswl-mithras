package cn.zswltech.mithras.workbench.application.cardcal.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkbenchFundRepayCashFlow {
    private String writeOffState;
    private Long repayAmount;
}
