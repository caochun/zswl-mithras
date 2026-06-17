package cn.zswltech.mithras.liquidity.snapshot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流动性指标计算所需的授信额度快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityCreditLimitSnapshot {

    /**
     * 授信总额度，单位：毫厘
     */
    private Long totalLimit;

    /**
     * 已占用授信额度，单位：毫厘
     */
    private Long occupyTotalLimit;
}
