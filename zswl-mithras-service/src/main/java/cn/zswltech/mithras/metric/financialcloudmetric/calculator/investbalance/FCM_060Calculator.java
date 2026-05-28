package cn.zswltech.mithras.metric.financialcloudmetric.calculator.investbalance;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.ConditionKey;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.Region;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description:1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_060Calculator extends InventoryInvestmentBalanceCalculator {


    @Override
    public String metricCode() {
        return "FCM_060";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        return super.calculate(dateTime);
    }

    @Override
    public Pair<ConditionKey, String> condition() {
        return Pair.of(ConditionKey.REGION,
                Region.SHANXI.code());
    }
}
