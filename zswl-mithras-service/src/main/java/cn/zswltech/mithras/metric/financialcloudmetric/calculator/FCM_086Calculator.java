package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description: 新增投放占比（%）  FCM_085/FCM_016 指标：当年新增客户投放的金额/当年累计新增投放规模
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_086Calculator extends TwoMetricCalculator {
    @Resource
    private FinancialCloudMetricValueService metricValueService;

    @Override
    public String metricCode() {
        return "FCM_086";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {

        FinancialCloudMetricValue fcm085 = metricValueService.getMetricValue("FCM_085", dateTime);
        if (fcm085.getMetricValue() != null && "0".equals(fcm085.getMetricValue())) {
            return BigDecimal.ZERO;
        }
        FinancialCloudMetricValue fcm016 = metricValueService.getMetricValue("FCM_016", dateTime);
        return division(fcm085, fcm016, 4);
    }
}
