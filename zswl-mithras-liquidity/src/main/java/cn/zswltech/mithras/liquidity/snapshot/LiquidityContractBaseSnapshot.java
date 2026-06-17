package cn.zswltech.mithras.liquidity.snapshot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流动性错配计算所需的合同基础快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityContractBaseSnapshot {

    private String contractCode;
}
