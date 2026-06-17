package cn.zswltech.mithras.metric.financialcloudmetric.port;

import java.time.LocalDate;
import java.util.List;

public interface FinancialCloudAccountBalancePort {

    List<FinancialCloudAccountBalanceSnapshot> listByDateRange(LocalDate start, LocalDate end);
}
