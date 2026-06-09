package cn.zswltech.mithras.metric.application.job;

import java.time.LocalDate;

public interface JinKongMonthlyReportPort {

    void syncAccountBalanceData(int year, int month);

    void syncBcmFflexfiledAssistMfByDept(int year, int month);

    void jinKongSyncAsset(LocalDate localDate);

    void jinKongSyncProfit(LocalDate localDate);

    void jinKongSyncCashFlow(LocalDate localDate);
}
