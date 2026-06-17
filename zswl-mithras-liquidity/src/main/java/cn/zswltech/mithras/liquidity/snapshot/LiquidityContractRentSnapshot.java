package cn.zswltech.mithras.liquidity.snapshot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 流动性错配计算所需的合同租金现金流快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityContractRentSnapshot {

    private Long contractId;

    private LocalDate cashFlowDate;

    private Long rent;
}
