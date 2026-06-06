package cn.zswltech.mithras.riskcontrol.application.job;

import java.time.LocalDate;

public interface RiskControlJobService {

    void syncRiskControl();

    void startWarnFlow(LocalDate targetDate);

    void riskControlPaymentFlow(String jobParam);
}
