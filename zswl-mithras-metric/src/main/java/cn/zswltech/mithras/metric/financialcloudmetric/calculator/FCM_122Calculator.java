package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * @description: 浙商租赁收入利润率 119/117  (万元)/(亿元)   % ==> 改： 指标：利润总额（亏损总额以“－”号填列）@本年累计数 / 营业总收入@本年累计数
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_122Calculator extends FinancialReportRelatedCalculator implements Secondary {

    @Override
    public String metricCode() {
        return "FCM_122";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        RiskMetricFactor factor1 = getFactor("四、利润总额（亏损总额以“－”号填列）@本年累计数", "利润表", dateTime);
        RiskMetricFactor factor2 = getFactor("一、营业总收入@本年累计数", "利润表", dateTime);
        return new BigDecimal(factor1.getFactorValue())
                .multiply(new BigDecimal(1000000))
                .divide(new BigDecimal(factor2.getFactorValue()), 4, RoundingMode.HALF_UP);
    }
}
