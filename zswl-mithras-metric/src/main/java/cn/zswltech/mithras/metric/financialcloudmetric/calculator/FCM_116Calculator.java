package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueReader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: FCM_21/FCM_111 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:59
 */
@Component
public class FCM_116Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueReader cloudMetricValueService;

    @Override
    public String metricCode() {
        return "FCM_116";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm21 = cloudMetricValueService.getMetricValue("FCM_021", dateTime);
        FinancialCloudMetricValue fcm111 = cloudMetricValueService.getMetricValue("FCM_111", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        return division(fcm21, fcm111, 4);
    }
}
