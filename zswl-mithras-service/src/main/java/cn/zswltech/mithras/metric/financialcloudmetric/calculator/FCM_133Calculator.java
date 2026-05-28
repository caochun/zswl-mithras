package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * @description: 资产负债率  负债总额/资产总额 OK
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_133Calculator extends FinancialReportRelatedCalculator {

    @Override
    public String metricCode() {
        return "FCM_133";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        RiskMetricFactor factor1 = getFactor("负债合计@期末余额", "资产负债表", dateTime);
        RiskMetricFactor factor2 = getFactor("资产总计@期末余额", "资产负债表", dateTime);
        return new BigDecimal(factor1.getFactorValue())
                .multiply(BigDecimal.valueOf(1000000))
                .divide(new BigDecimal(factor2.getFactorValue()), 4, RoundingMode.HALF_UP);
    }
}
