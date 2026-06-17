package cn.zswltech.mithras.liquidity.snapshot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 流动性指标计算所需的还本付息计划现金流快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityFundReceiptFlowPlanSnapshot {

    private Long receiptRepayId;

    private Integer cashFlowPhase;

    private String cashFlowCode;

    private String cashFlowItem;

    private LocalDate cashFlowDate;

    private Long principalAmount;

    private Long interestAmount;
}
