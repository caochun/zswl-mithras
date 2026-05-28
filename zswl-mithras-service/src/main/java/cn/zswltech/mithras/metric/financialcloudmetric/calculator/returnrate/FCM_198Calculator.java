package cn.zswltech.mithras.metric.financialcloudmetric.calculator.returnrate;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.ConditionKey;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.Department;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.TimeDimension;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description: 部门 整体新增投放平均收益率
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_198Calculator extends AverageReturnRateCalculator {


    @Override
    public String metricCode() {
        return "FCM_198";
    }

    @Override
    public Pair<ConditionKey, String> condition() {
        return Pair.of(ConditionKey.DEPARTMENT, Department.XNYYWB.name());
    }

    @Override
    protected TimeDimension timeDimension() {
        return TimeDimension.TOTAL;
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        return super.calculate(dateTime);
    }
}
