package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueReader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

/**
 * @description: 累计新增投放平均收益率偏离度  FCM_95-FCM_105 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_100Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueReader metricValueService;

    @Override
    public String metricCode() {
        return "FCM_100";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm95 = metricValueService.getMetricValue("FCM_095", dateTime);
        if (fcm95.getValue() != null && "0".equals(fcm95.getValue())) {
            return BigDecimal.ZERO;
        }
        FinancialCloudMetricValue fcm105 = metricValueService.getMetricValue("FCM_087", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        if (Objects.nonNull(fcm105) && Objects.nonNull(fcm95.getMetricValue()) && Objects.nonNull(fcm105.getValue())) {
            return new BigDecimal(fcm95.getMetricValue()).subtract(new BigDecimal(fcm105.getValue()));
        }
        return BigDecimal.ZERO;
    }
}
