package cn.zswltech.mithras.liquidity.snapshot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Liquidity calculation input for planned rent collections.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityCollectionPlanSnapshot {

    private Long id;

    private Long contractId;

    private String code;

    private Integer phase;

    private LocalDate planCollectionDate;

    private Long planCollectionAmount;
}
