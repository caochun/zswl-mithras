package cn.zswltech.mithras.liquidity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流动性指标计算所需的融资质押/监管快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityFinancingPledgeSnapshot {

    private Long financingId;

    private Long contractId;

    private Boolean pledge;

    private Boolean supervise;
}
