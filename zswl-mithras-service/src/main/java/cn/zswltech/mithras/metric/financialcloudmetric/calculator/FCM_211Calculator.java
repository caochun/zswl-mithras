package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
import cn.zswltech.mithras.service.util.StringUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: FCM_179/FCM_207 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:59
 */
@Component
public class FCM_211Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueService cloudMetricValueService;

    @Override
    public String metricCode() {
        return "FCM_211";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm179 = cloudMetricValueService.getMetricValue("FCM_179", dateTime);
        FinancialCloudMetricValue fcm207 = cloudMetricValueService.getMetricValue("FCM_207", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        return division(fcm179, fcm207, 4);
    }
}
