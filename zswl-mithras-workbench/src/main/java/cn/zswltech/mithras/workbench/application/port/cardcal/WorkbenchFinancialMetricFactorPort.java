package cn.zswltech.mithras.workbench.application.port.cardcal;

import java.time.LocalDate;

public interface WorkbenchFinancialMetricFactorPort {
    Long getFactorValue(String name, String table, LocalDate dateTime);
}
