package cn.zswltech.mithras.liquidity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 流动性指标计算所需的直融主体快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityDirectFinancingSnapshot {

    private Long id;

    private String productName;

    private String financingCode;

    private String directFinancingType;

    private LocalDate carryInterestTime;
}
