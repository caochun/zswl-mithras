package cn.zswltech.mithras.liquidity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流动性指标计算所需的间融还款账户快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityFinancingPayAccountSnapshot {

    private Long financingId;

    private Long bankAccountId;

    private String accountCategory;
}
