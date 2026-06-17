package cn.zswltech.mithras.riskcontrol.application.port;

public class RiskControlInterceptFact {

    private final Long clientId;
    private final Long amount;

    public RiskControlInterceptFact(Long clientId, Long amount) {
        this.clientId = clientId;
        this.amount = amount;
    }

    public Long getClientId() {
        return clientId;
    }

    public Long getAmount() {
        return amount;
    }
}
