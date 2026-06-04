package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.Department;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * @description:1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_034Calculator extends DepartmentPerCapitalCalculator {


    @Override
    public String metricCode() {
        return "FCM_034";
    }

    @Override
    protected String departmentCode() {
        return Department.GGSY.name();
    }

    @Override
    protected String totalMetricCode() {
        return "FCM_021";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        return super.calculate(dateTime);
    }

    @Override
    protected BigDecimal doCal(int personCount) {
        BigDecimal bigDecimal = NEWLY_ADDED_INVESTMENT_SCALE.get(departmentCode());
        if (bigDecimal != null) {
            return bigDecimal.divide(new BigDecimal(personCount), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

}
