package cn.zswltech.mithras.liquidity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流动性指标计算所需的还本付息主表快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityFundReceiptRepaySnapshot {

    private Long id;

    private Long financingId;

    private String financingType;
}
