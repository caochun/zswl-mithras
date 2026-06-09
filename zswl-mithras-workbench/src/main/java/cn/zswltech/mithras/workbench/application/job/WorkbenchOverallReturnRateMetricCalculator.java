package cn.zswltech.mithras.workbench.application.job;

import java.time.LocalDate;

public interface WorkbenchOverallReturnRateMetricCalculator {

    void calculate(LocalDate dateTime);
}
