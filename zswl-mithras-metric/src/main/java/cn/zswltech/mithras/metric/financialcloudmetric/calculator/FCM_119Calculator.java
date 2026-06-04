package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description: 累计利润总额 财务报表取数 万元 OK
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_119Calculator extends FinancialReportRelatedCalculator {

    public static final String FACTOR_NAME = "四、利润总额（亏损总额以“－”号填列）@本年累计数";
    public static final String FACTOR_TABLE = "利润表";


    @Override
    public String metricCode() {
        return "FCM_119";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {

        RiskMetricFactor factor = getFactor(FACTOR_NAME, FACTOR_TABLE, dateTime);
        return new BigDecimal(factor.getFactorValue());
    }
}
