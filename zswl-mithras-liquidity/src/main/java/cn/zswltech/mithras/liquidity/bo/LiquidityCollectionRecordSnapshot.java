package cn.zswltech.mithras.liquidity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Liquidity calculation input for actual rent collection write-offs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityCollectionRecordSnapshot {

    private Long collectionId;

    private LocalDate collectionDate;

    private Long collectionAmount;
}
