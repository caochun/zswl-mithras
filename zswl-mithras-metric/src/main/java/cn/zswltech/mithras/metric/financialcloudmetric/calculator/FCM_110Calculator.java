
package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.Department;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author bigbear
 * @date 2025/4/25 15:53
 * @description
 */
@Component
public class FCM_110Calculator extends DepartmentBaseCalculator {
    @Override
    public String metricCode() {
        return "FCM_110";
    }

    @Override
    public String departmentCode() {
        return Department.XJZZHXJJTD.name();
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        return super.calculate(dateTime);
    }
}
