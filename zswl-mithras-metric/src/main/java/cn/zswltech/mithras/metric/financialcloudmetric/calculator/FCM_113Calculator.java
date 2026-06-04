package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueReader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: FCM_18/FCM_108 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:58
 */
@Component
public class FCM_113Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueReader cloudMetricValueService;

    @Override
    public String metricCode() {
        return "FCM_113";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm18 = cloudMetricValueService.getMetricValue("FCM_018", dateTime);
        FinancialCloudMetricValue fcm108 = cloudMetricValueService.getMetricValue("FCM_108", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        return division(fcm18, fcm108, 4);
    }
}
