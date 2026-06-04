package cn.zswltech.mithras.metric.service;

import java.time.LocalDate;

public interface RiskMetricFactorRefreshClient {

    boolean refreshAsset(LocalDate date);

    boolean refreshProfit(LocalDate date);

    boolean refreshCashFlow(LocalDate date);

    boolean refreshSubjectBalance(LocalDate date);

    void testProfitSync();
}
