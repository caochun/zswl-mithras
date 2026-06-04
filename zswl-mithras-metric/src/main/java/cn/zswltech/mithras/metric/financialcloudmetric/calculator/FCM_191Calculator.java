package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * @description:1
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Slf4j
@Component
public class FCM_191Calculator extends DepartmentPerCapitalCalculator {


    @Override
    public String metricCode() {
        return "FCM_191";
    }

    @Override
    protected String departmentCode() {
        return Department.LSCYYWB.name();
    }

    @Override
    protected String totalMetricCode() {
        return "FCM_187";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        return super.calculate(dateTime);
    }

    @Override
    protected BigDecimal doCal(int personCount) {
        log.info("部门：{}，部门人数：{}", Department.valueOf(departmentCode()).getName(), personCount);
        BigDecimal bigDecimal = STOCK_INVESTMENT_BALANCE.get(departmentCode());
        if (bigDecimal != null) {
            return bigDecimal.divide(new BigDecimal(personCount), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

}
