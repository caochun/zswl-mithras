package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: FCM_20/FCM_110 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:59
 */
@Component
public class FCM_115Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueService cloudMetricValueService;

    @Override
    public String metricCode() {
        return "FCM_115";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm20 = cloudMetricValueService.getMetricValue("FCM_020", dateTime);
        FinancialCloudMetricValue fcm110 = cloudMetricValueService.getMetricValue("FCM_110", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        return division(fcm20, fcm110, 4);
    }
}
