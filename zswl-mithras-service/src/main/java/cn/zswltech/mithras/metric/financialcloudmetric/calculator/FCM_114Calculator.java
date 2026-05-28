package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: FCM_19/FCM_109 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:59
 */
@Component
public class FCM_114Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueService cloudMetricValueService;

    @Override
    public String metricCode() {
        return "FCM_114";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm19 = cloudMetricValueService.getMetricValue("FCM_019", dateTime);
        FinancialCloudMetricValue fcm109 = cloudMetricValueService.getMetricValue("FCM_109", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        return division(fcm19, fcm109, 4);
    }
}
