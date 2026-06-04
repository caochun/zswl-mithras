package cn.zswltech.mithras.metric.financialcloudmetric.calculator.investbalance;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.DepartmentPerCapitalCalculator;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.ConditionKey;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.Department;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_077Calculator extends InventoryInvestmentBalanceCalculator {


    @Override
    public String metricCode() {
        return "FCM_077";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        return super.calculateDeptCumulative(dateTime);
    }

    @Override
    public Pair<ConditionKey, String> condition() {
        return Pair.of(ConditionKey.DEPARTMENT, Department.XJZZHXJJTD.name());
    }
}
