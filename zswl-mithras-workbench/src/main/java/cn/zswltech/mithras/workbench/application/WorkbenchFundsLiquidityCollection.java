package cn.zswltech.mithras.workbench.application;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class WorkbenchFundsLiquidityCollection {
    private LocalDate planCollectionDate;
    private Long planCollectionAmount;
    private Long collectionAmount;
}
