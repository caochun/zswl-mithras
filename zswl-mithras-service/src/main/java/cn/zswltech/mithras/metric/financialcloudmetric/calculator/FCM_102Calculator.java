package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: 累计新增投放平均收益率偏离度  FCM_97-FCM_105 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_102Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueService metricValueService;

    @Override
    public String metricCode() {
        return "FCM_102";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm97 = metricValueService.getMetricValue("FCM_097", dateTime);
        if (fcm97.getMetricValue() != null && "0".equals(fcm97.getMetricValue())) {
            return BigDecimal.ZERO;
        }
        FinancialCloudMetricValue fcm105 = metricValueService.getMetricValue("FCM_087", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        return new BigDecimal(fcm97.getMetricValue()).subtract(new BigDecimal(fcm105.getValue()));
    }
}
