package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description: 累计营业收入（亿元） ==> 改： 利润表：营业总收入@本年累计数
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_117Calculator  extends FinancialReportRelatedCalculator {
    public static final String FACTOR_NAME = "一、营业总收入@本年累计数";
    public static final String FACTOR_TABLE = "利润表";


    @Override
    public String metricCode() {
        return "FCM_117";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        RiskMetricFactor factor = getFactor(FACTOR_NAME, FACTOR_TABLE, dateTime);
        return new BigDecimal(factor.getFactorValue());
    }
}
