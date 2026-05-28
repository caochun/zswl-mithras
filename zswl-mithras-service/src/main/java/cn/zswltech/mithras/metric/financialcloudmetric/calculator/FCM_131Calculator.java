package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * @description: 货币资金余额/期末有息负债余额 %
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_131Calculator extends FinancialReportRelatedCalculator {

    @Override
    public String metricCode() {
        return "FCM_131";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        RiskMetricFactor factor1 = getFactor("货币资金@期末余额", "资产负债表", dateTime);
        RiskMetricFactor factor2 = getFactor("长期借款_银行借款@期末余额@贷方金额", "科目余额表", dateTime);
        RiskMetricFactor factor3 = getFactor("短期借款_银行借款@期末余额@贷方金额", "科目余额表", dateTime);
        RiskMetricFactor factor4 = getFactor("应付债券_公司债券_债券面值@期末余额@贷方金额", "科目余额表", dateTime);
        RiskMetricFactor factor5 = getFactor("应付债券_企业债券_债券面值@期末余额@贷方金额", "科目余额表", dateTime);
        RiskMetricFactor factor6 = getFactor("长期应付款_其他@期末余额@贷方金额", "科目余额表", dateTime);
        BigDecimal balance = BigDecimal.valueOf(factor2.getFactorValue())
                .add(BigDecimal.valueOf(factor3.getFactorValue()))
                .add(BigDecimal.valueOf(factor4.getFactorValue()))
                .add(BigDecimal.valueOf(factor5.getFactorValue()))
                .add(BigDecimal.valueOf(factor6.getFactorValue()));
        return new BigDecimal(factor1.getFactorValue())
                .multiply(BigDecimal.valueOf(1000000))
                .divide(balance, 4, RoundingMode.HALF_UP);
    }
}
