package cn.zswltech.mithras.liquidity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 流动性指标计算所需的间融主体快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityFinancingSnapshot {

    private Long id;

    private String financingCode;

    private LocalDate actualLoanDate;
}
