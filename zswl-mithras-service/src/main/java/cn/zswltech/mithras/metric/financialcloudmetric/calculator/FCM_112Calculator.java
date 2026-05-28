package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: FCM_17/FCM_107 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_112Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueService cloudMetricValueService;

    @Override
    public String metricCode() {
        return "FCM_112";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm17 = cloudMetricValueService.getMetricValue("FCM_017", dateTime);
        FinancialCloudMetricValue fcm107 = cloudMetricValueService.getMetricValue("FCM_107", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        return division(fcm17, fcm107, 4);
    }
}
