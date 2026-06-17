package cn.zswltech.mithras.liquidity.snapshot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流动性指标计算所需的还本付息核销明细快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityFundReceiptFlowDetailSnapshot {

    private Long receiptRepayId;

    private String cashFlowCode;

    private Long totalAmount;
}
