package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:22
 */
public interface FinancialCloudMetricCalculator {

    String metricCode();

    BigDecimal calculate(LocalDate dateTime);

}
