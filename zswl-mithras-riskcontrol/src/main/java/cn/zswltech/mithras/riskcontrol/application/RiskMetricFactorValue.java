package cn.zswltech.mithras.riskcontrol.application;

import java.time.LocalDate;

public class RiskMetricFactorValue {

    private final Long factorValue;
    private final LocalDate factorDate;

    public RiskMetricFactorValue(Long factorValue) {
        this(factorValue, null);
    }

    public RiskMetricFactorValue(Long factorValue, LocalDate factorDate) {
        this.factorValue = factorValue;
        this.factorDate = factorDate;
    }

    public Long getFactorValue() {
        return factorValue;
    }

    public LocalDate getFactorDate() {
        return factorDate;
    }
}
