package cn.zswltech.mithras.liquidity.snapshot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 流动性指标计算所需的直融实际还款快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityDirectFinancingRepayActualSnapshot {

    private Long financingId;

    private LocalDate repayDate;

    private Long repayAmount;
}
