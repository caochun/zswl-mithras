package cn.zswltech.mithras.metric.financialcloudmetric.port;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialCloudAccountBalanceSnapshot {

    private Long rentReflowAmount;

    private Long repayAmount;

    private Long repayEditAmount;
}
