package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueReader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: FCM_177/FCM_205 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:59
 */
@Component
public class FCM_209Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueReader cloudMetricValueService;

    @Override
    public String metricCode() {
        return "FCM_209";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm177 = cloudMetricValueService.getMetricValue("FCM_177", dateTime);
        FinancialCloudMetricValue fcm205 = cloudMetricValueService.getMetricValue("FCM_205", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        return division(fcm177, fcm205, 4);
    }
}
