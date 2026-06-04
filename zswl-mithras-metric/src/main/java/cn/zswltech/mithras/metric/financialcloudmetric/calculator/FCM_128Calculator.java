package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description: 货币资金余额（万元） OK
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_128Calculator extends FinancialReportRelatedCalculator {

    public static final String FACTOR_NAME = "货币资金@期末余额";
    public static final String FACTOR_TABLE = "资产负债表";

    @Override
    public String metricCode() {
        return "FCM_128";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        RiskMetricFactor factor = getFactor(FACTOR_NAME, FACTOR_TABLE, dateTime);
        return new BigDecimal(factor.getFactorValue());
    }
}
