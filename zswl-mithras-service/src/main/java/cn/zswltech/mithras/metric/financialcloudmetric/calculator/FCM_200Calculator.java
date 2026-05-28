package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: 累计新增投放平均收益率偏离度  FCM_196-FCM_105 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_200Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueService metricValueService;

    @Override
    public String metricCode() {
        return "FCM_200";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm196 = metricValueService.getMetricValue("FCM_196", dateTime);
        if (fcm196.getMetricValue() != null && "0".equals(fcm196.getMetricValue())) {
            return BigDecimal.ZERO;
        }
        FinancialCloudMetricValue fcm105 = metricValueService.getMetricValue("FCM_087", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        return new BigDecimal(fcm196.getMetricValue()).subtract(new BigDecimal(fcm105.getValue()));
    }
}



