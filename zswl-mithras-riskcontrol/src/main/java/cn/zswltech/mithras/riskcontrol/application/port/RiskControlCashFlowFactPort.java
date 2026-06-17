package cn.zswltech.mithras.riskcontrol.application.port;

public interface RiskControlCashFlowFactPort {

    long totalPaidAmount();

    long totalFirstRentCollectionAmount();

    long totalRentPrincipalCollectionAmount();
}
