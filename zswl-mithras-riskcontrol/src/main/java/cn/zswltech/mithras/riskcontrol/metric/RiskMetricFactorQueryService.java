package cn.zswltech.mithras.riskcontrol.metric;

import java.time.LocalDate;

public interface RiskMetricFactorQueryService {

    RiskMetricFactorValue newestFactor(String factorName, String factorTable, LocalDate factorQueryDate);
}
