package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueReader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: FCM_176/FCM_204 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:59
 */
@Component
public class FCM_208Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueReader cloudMetricValueService;

    @Override
    public String metricCode() {
        return "FCM_208";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm176 = cloudMetricValueService.getMetricValue("FCM_180", dateTime);
        FinancialCloudMetricValue fcm204 = cloudMetricValueService.getMetricValue("FCM_204", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        return division(fcm176, fcm204, 10);
    }
}
