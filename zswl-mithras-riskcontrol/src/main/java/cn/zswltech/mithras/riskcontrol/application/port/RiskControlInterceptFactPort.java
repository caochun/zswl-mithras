package cn.zswltech.mithras.riskcontrol.application.port;

import java.util.Optional;

public interface RiskControlInterceptFactPort {

    Optional<RiskControlInterceptFact> projectEstablishFact(Long projEstablishId);

    Optional<RiskControlInterceptFact> paymentApplyFact(Long paymentId);
}
