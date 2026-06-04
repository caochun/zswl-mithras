package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueReader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: FCM_178/FCM_206 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:59
 */
@Component
public class FCM_210Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueReader cloudMetricValueService;

    @Override
    public String metricCode() {
        return "FCM_210";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm178 = cloudMetricValueService.getMetricValue("FCM_178", dateTime);
        FinancialCloudMetricValue fcm206 = cloudMetricValueService.getMetricValue("FCM_206", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        return division(fcm178, fcm206, 4);
    }
}
