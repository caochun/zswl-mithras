package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description: 未来六月累计资金缺口（万元） 未来六月还款额-未来六月预计回款额 OK
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_127Calculator extends TwoMetricCalculator {

    @Resource
    private FinancialCloudMetricValueService metricValueService;

    @Override
    public String metricCode() {
        return "FCM_127";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        FinancialCloudMetricValue fcm129 = metricValueService.getMetricValue("FCM_129", dateTime);
        FinancialCloudMetricValue fcm130 = metricValueService.getMetricValue("FCM_130", dateTime);
        return subtraction(fcm129, fcm130).multiply(new BigDecimal(10000L));
    }
}
