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
 * @description: 累计新增投放平均收益率偏离度  FCM_96-FCM_105 1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_101Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueReader metricValueService;

    @Override
    public String metricCode() {
        return "FCM_101";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm96 = metricValueService.getMetricValue("FCM_096", dateTime);
        if (fcm96.getMetricValue() != null && "0".equals(fcm96.getMetricValue())) {
            return BigDecimal.ZERO;
        }
        FinancialCloudMetricValue fcm105 = metricValueService.getMetricValue("FCM_087", dateTime.with(TemporalAdjusters.firstDayOfYear()));
        if (Objects.isNull(fcm105) || Objects.isNull(fcm105.getValue())) {
            return new BigDecimal(fcm96.getMetricValue()).subtract(BigDecimal.ZERO);
        } else {
            return new BigDecimal(fcm96.getMetricValue()).subtract(new BigDecimal(fcm105.getValue()));
        }
    }
}
