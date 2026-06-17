package cn.zswltech.mithras.liquidity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流动性指标计算所需的融资机构快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityFinancingOrganizationSnapshot {

    private Long id;

    private String organizationName;
}
