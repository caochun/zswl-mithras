package cn.zswltech.mithras.metric.financialcloudmetric.calculator.accincrease;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.ConditionKey;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description: 当年累计新增投放规模（万元）: 实付日期在系统当年的付款核销的实付金额之和
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_016Calculator extends AccumulativeIncreaseCalculator {


    @Override
    public String metricCode() {
        return "FCM_016";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        return super.calculate(dateTime);
    }

    @Override
    public Pair<ConditionKey, String> condition() {
        return null;
    }
}
